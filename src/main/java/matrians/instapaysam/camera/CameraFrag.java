package matrians.instapaysam.camera;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.params.Face;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ExifInterface;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.Rational;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.camera.SCamera;
import com.samsung.android.sdk.camera.SCameraCaptureSession;
import com.samsung.android.sdk.camera.SCameraCharacteristics;
import com.samsung.android.sdk.camera.SCameraDevice;
import com.samsung.android.sdk.camera.SCameraManager;
import com.samsung.android.sdk.camera.SCaptureFailure;
import com.samsung.android.sdk.camera.SCaptureRequest;
import com.samsung.android.sdk.camera.SCaptureResult;
import com.samsung.android.sdk.camera.SDngCreator;
import com.samsung.android.sdk.camera.STotalCaptureResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import matrians.instapaysam.R;

@SuppressWarnings("FieldCanBeLocal")
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CameraFrag extends Fragment {
    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = "CameraSingle";

    int screenWidth;
    int screenHeight;

    /**
     * Maximum preview width app will use.
     */
    private static final int MAX_PREVIEW_WIDTH = 1920;
    /**
     * Maximum preview height app will use.
     */
    private static final int MAX_PREVIEW_HEIGHT = 1080;
    /**
     * Conversion from device rotation to DNG orientation
     */
    private static final SparseIntArray DNG_ORIENTATION = new SparseIntArray();

    static {
        DNG_ORIENTATION.append(0, ExifInterface.ORIENTATION_NORMAL);
        DNG_ORIENTATION.append(90, ExifInterface.ORIENTATION_ROTATE_90);
        DNG_ORIENTATION.append(180, ExifInterface.ORIENTATION_ROTATE_180);
        DNG_ORIENTATION.append(270, ExifInterface.ORIENTATION_ROTATE_270);
    }

    private SCamera mSCamera;
    private SCameraManager mSCameraManager;
    private SCameraDevice mSCameraDevice;
    private SCameraCaptureSession mSCameraSession;
    private SCameraCharacteristics mCharacteristics;
    private SCaptureRequest.Builder mPreviewBuilder;
    private SCaptureRequest.Builder mCaptureBuilder;
    /**
     * Current Preview Size.
     */
    private Size mPreviewSize;
    /**
     * Current Picture Size.
     */
    private Size mPictureSize;
    /**
     * ID of the current {@link SCameraDevice}.
     */
    private String mCameraId;
    /**
     * Lens facing. Camera with this facing will be opened
     */
    private int mLensFacing;
    private List<Integer> mLensFacingList;
    /**
     * Image saving format.
     */
    private int mImageFormat;
    private List<Integer> mImageFormatList;

    /**
     * An {@link matrians.instapaysam.camera.AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView mTextureView;
    private ImageReader mJpegReader;
    private ImageReader mRawReader;
    private ImageSaver mImageSaver = new ImageSaver();
    /**
     * A camera related listener/callback will be posted in this handler.
     */
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundHandlerThread;
    /**
     * A image saving worker Runnable will be posted to this handler.
     */
    private Handler mImageSavingHandler;
    private HandlerThread mImageSavingHandlerThread;
    private BlockingQueue<SCaptureResult> mCaptureResultQueue;
    /**
     * An orientation listener for jpeg orientation
     */
    private OrientationEventListener mOrientationListener;
    private int mLastOrientation = 0;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    /**
     * True if {@link SCaptureRequest#CONTROL_AF_TRIGGER} is triggered.
     */
    private boolean isAFTriggered;
    /**
     * True if {@link SCaptureRequest#CONTROL_AE_PRECAPTURE_TRIGGER} is triggered.
     */
    private boolean isAETriggered;
    /**
     * Current app state.
     */
    private CAMERA_STATE mState = CAMERA_STATE.IDLE;

    private BarcodeDetector detector;

    /*private SCameraCaptureSession.StateCallback mSessionStateCallback = new SCameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(SCameraCaptureSession sCameraCaptureSession) {
            switch (getState()) {
                case PREVIEW:
                    SparseArray<Barcode> barcodes = detector.detect(frame);
                    for (int i = 0; i < barcodes.size(); i++) {
                        Barcode thisCode = barcodes.valueAt(i);
                        Log.d("BARCODE", thisCode.rawValue);
                    }
                    break;
            }
        }

        @Override
        public void onConfigureFailed(SCameraCaptureSession sCameraCaptureSession) {

        }
    };*/

    /**
     * A {@link SCameraCaptureSession.CaptureCallback} for {@link SCameraCaptureSession#setRepeatingRequest(SCaptureRequest, SCameraCaptureSession.CaptureCallback, Handler)}
     */
    private SCameraCaptureSession.CaptureCallback mSessionCaptureCallback = new SCameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureCompleted(SCameraCaptureSession session, SCaptureRequest request, STotalCaptureResult result) {
            // Remove comment, if you want to check request/result from console log.
            // dumpCaptureResultToLog(result);
            // dumpCaptureRequestToLog(request);

            boolean sceneOverride = false;

            if (mPreviewBuilder.get(SCaptureRequest.CONTROL_MODE) == SCaptureRequest.CONTROL_MODE_USE_SCENE_MODE &&
                    mPreviewBuilder.get(SCaptureRequest.CONTROL_SCENE_MODE) != SCaptureRequest.CONTROL_SCENE_MODE_FACE_PRIORITY) {
                sceneOverride = true;
            }

            // Depends on the current state and capture result, app will take next action.
            switch (getState()) {

                case IDLE:
                case TAKE_PICTURE:
                case CLOSING:
                    // do nothing
                    break;
                case PREVIEW:
                    break;

                // If AF is triggered and AF_STATE indicates AF process is finished, app will trigger AE pre-capture.
                case WAIT_AF: {
                    if (isAFTriggered) {
                        int afState = result.get(SCaptureResult.CONTROL_AF_STATE);
                        // Check if AF is finished.
                        if (SCaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                                SCaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState ||
                                (sceneOverride && // if scene mode is activated, 3A mode can be changed
                                        (result.get(SCaptureResult.CONTROL_AF_MODE) == null || // for the device that does not report the AF_MODE for scene mode.
                                                result.get(SCaptureResult.CONTROL_AF_MODE) == SCaptureResult.CONTROL_AF_MODE_OFF ||
                                                result.get(SCaptureResult.CONTROL_AF_MODE) == SCaptureResult.CONTROL_AF_MODE_EDOF))) {

                            // If AE mode is off or device is legacy device then skip AE pre-capture.
                            if (result.get(SCaptureResult.CONTROL_AE_MODE) != SCaptureResult.CONTROL_AE_MODE_OFF &&
                                    mCharacteristics.get(SCameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) != SCameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
                                triggerAE();
                            } else {
                                takePicture();
                            }
                            isAFTriggered = false;
                        }
                    }
                    break;
                }

                // If AE is triggered and AE_STATE indicates AE pre-capture process is finished, app will take a picture.
                case WAIT_AE: {
                    if (isAETriggered) {
                        Integer aeState = result.get(SCaptureResult.CONTROL_AE_STATE);
                        if (null == aeState || // Legacy device might have null AE_STATE. However, this should not be happened as we skip triggerAE() for legacy device
                                SCaptureResult.CONTROL_AE_STATE_CONVERGED == aeState ||
                                SCaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED == aeState ||
                                SCaptureResult.CONTROL_AE_STATE_LOCKED == aeState ||
                                (sceneOverride && // if scene mode is activated, 3A mode can be changed
                                        (result.get(SCaptureResult.CONTROL_AE_MODE) == null || // for the device that does not report the AE_MODE for scene mode.
                                                result.get(SCaptureResult.CONTROL_AE_MODE) == SCaptureResult.CONTROL_AE_MODE_OFF))) {
                            takePicture();
                            isAETriggered = false;
                        }
                    }
                    break;
                }
            }
        }
    };
    /**
     * A {@link ImageReader.OnImageAvailableListener} for still capture.
     */
    private ImageReader.OnImageAvailableListener mImageCallback = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            if (mImageFormat == ImageFormat.JPEG) mImageSaver.save(reader.acquireNextImage(), createFileName() + ".jpg");
            else mImageSaver.save(reader.acquireNextImage(), createFileName() + ".dng");
        }
    };

    @Override
    public void onPause() {
        setState(CAMERA_STATE.CLOSING);

        setOrientationListener(false);

        stopBackgroundThread();
        closeCamera();

        mSCamera = null;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        setState(CAMERA_STATE.IDLE);

        startBackgroundThread();

        // initialize SCamera
        mSCamera = new SCamera();
        try {
            mSCamera.initialize(getActivity());
        } catch (SsdkUnsupportedException e) {
            showAlertDialog("Fail to initialize SCamera.", true);
            return;
        }

        mCaptureResultQueue = new LinkedBlockingQueue<>();

        setOrientationListener(true);
        createUI();
        checkRequiredFeatures();
        openCamera(mLensFacing);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        detector = new BarcodeDetector.Builder(getActivity()).build();
        if(!detector.isOperational()) {
            Toast.makeText(getActivity(), "Could not set up the detector!", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }
    }

    private void checkRequiredFeatures() {
        try {
            // Find available lens facing value for this device
            Set<Integer> lensFacings = new HashSet<>();
            for (String id : mSCamera.getSCameraManager().getCameraIdList()) {
                SCameraCharacteristics cameraCharacteristics = mSCamera.getSCameraManager().getCameraCharacteristics(id);
                lensFacings.add(cameraCharacteristics.get(SCameraCharacteristics.LENS_FACING));
            }
            mLensFacingList = new ArrayList<>(lensFacings);

            mLensFacing = mLensFacingList.get(mLensFacingList.size() - 1);

            setDefaultJpegSize(mSCamera.getSCameraManager(), mLensFacing);

        } catch (CameraAccessException e) {
            showAlertDialog("Cannot access the camera.", true);
            Log.e(TAG, "Cannot access the camera.", e);
        }
    }

    /**
     * Closes a camera and release resources.
     */
    synchronized private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();

            if (mSCameraSession != null) {
                mSCameraSession.close();
                mSCameraSession = null;
            }

            if (mSCameraDevice != null) {
                mSCameraDevice.close();
                mSCameraDevice = null;
            }

            if (mJpegReader != null) {
                mJpegReader.close();
                mJpegReader = null;
            }

            if (mRawReader != null) {
                mRawReader.close();
                mRawReader = null;
            }

            mSCameraManager = null;
        } catch (InterruptedException e) {
            Log.e(TAG, "Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Configures requires transform {@link Matrix} to TextureView.
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        if (null == mTextureView || null == mPreviewSize) {
            return;
        }

        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else {
            matrix.postRotate(90 * rotation, centerX, centerY);
        }

        mTextureView.setTransform(matrix);
        mTextureView.getSurfaceTexture().setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
    }

    private boolean contains(final int[] array, final int key) {
        for (final int i : array) {
            if (i == key) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates file name based on current time.
     */
    private String createFileName() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeZone(TimeZone.getDefault());
        long dateTaken = calendar.getTimeInMillis();

        return DateFormat.format("yyyyMMdd_kkmmss", dateTaken).toString();
    }

    /**
     * Create a {@link SCameraCaptureSession} for preview.
     */
    synchronized private void createPreviewSession() {

        if (null == mSCamera
                || null == mSCameraDevice
                || null == mSCameraManager
                || null == mPreviewSize
                || !mTextureView.isAvailable())
            return;

        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();

            // Set default buffer size to camera preview size.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            Surface surface = new Surface(texture);

            // Creates SCaptureRequest.Builder for preview with output target.
            mPreviewBuilder = mSCameraDevice.createCaptureRequest(SCameraDevice.TEMPLATE_PREVIEW);
            mPreviewBuilder.addTarget(surface);

            // Creates SCaptureRequest.Builder for still capture with output target.
            mCaptureBuilder = mSCameraDevice.createCaptureRequest(SCameraDevice.TEMPLATE_STILL_CAPTURE);

            // Creates a SCameraCaptureSession here.
            List<Surface> outputSurface = new ArrayList<>();
            outputSurface.add(surface);
            outputSurface.add(mJpegReader.getSurface());
            if (mRawReader != null) outputSurface.add(mRawReader.getSurface());

            mSCameraDevice.createCaptureSession(outputSurface, new SCameraCaptureSession.StateCallback() {
                @Override
                public void onConfigureFailed(SCameraCaptureSession sCameraCaptureSession) {
                    if (getState() == CAMERA_STATE.CLOSING)
                        return;
                    showAlertDialog("Fail to create camera capture session.", true);
                    setState(CAMERA_STATE.IDLE);
                }

                @Override
                public void onConfigured(SCameraCaptureSession sCameraCaptureSession) {
                    if (getState() == CAMERA_STATE.CLOSING)
                        return;
                    mSCameraSession = sCameraCaptureSession;
                    startPreview();
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            showAlertDialog("Fail to create camera capture session.", true);
        }
    }

    /**
     * Prepares an UI, like button, dialog, etc.
     */
    private void createUI() {
        /*mSettingDialog = new SettingDialog(this);
        mSettingDialog.setOnCaptureRequestUpdatedListener(this);*/

        View view = getView();
        if (view == null) {
            Log.d(TAG, "View null");
            return;
        }

        view.findViewById(R.id.picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // take picture is only works under preview state.
                if (getState() == CAMERA_STATE.PREVIEW) {

                    // No AF lock is required for AF modes OFF/EDOF.
                    if (mPreviewBuilder.get(SCaptureRequest.CONTROL_AF_MODE) !=
                            SCaptureRequest.CONTROL_AF_MODE_OFF &&
                            mPreviewBuilder.get(SCaptureRequest.CONTROL_AF_MODE) !=
                                    SCaptureRequest.CONTROL_AF_MODE_EDOF) {
                        lockAF();

                        // No AE pre-capture is required for AE mode OFF or device is LEGACY.
                    } else if (mPreviewBuilder.get(SCaptureRequest.CONTROL_AE_MODE) !=
                            SCaptureRequest.CONTROL_AE_MODE_OFF &&
                            mCharacteristics.get(SCameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) !=
                                    SCameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
                        triggerAE();

                        // If AE/AF is skipped, run still capture directly.
                    } else {
                        takePicture();
                    }
                }
            }
        });

        mTextureView = (AutoFitTextureView) view.findViewById(R.id.texture);

        // Set SurfaceTextureListener that handle life cycle of TextureView
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                // "onSurfaceTextureAvailable" is called, which means that SCameraCaptureSession is not created.
                // We need to configure transform for TextureView and crate SCameraCaptureSession.
                configureTransform(width, height);
                createPreviewSession();
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return true;
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                // SurfaceTexture size changed, we need to configure transform for TextureView, again.
                configureTransform(width, height);
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
    }

    /**
     * Dump {@link SCaptureRequest} to console log.
     */
    private void dumpCaptureRequestToLog(SCaptureRequest request) {

        Log.v(TAG, "Dump of SCaptureRequest");
        for (SCaptureRequest.Key<?> key : request.getKeys()) {
            if (request.get(key) instanceof int[]) {
                Log.v(TAG, key.getName() + ": " + Arrays.toString((int[]) request.get(key)));
            } else if (request.get(key) instanceof float[]) {
                Log.v(TAG, key.getName() + ": " + Arrays.toString((float[]) request.get(key)));
            } else if (request.get(key) instanceof long[]) {
                Log.v(TAG, key.getName() + ": " + Arrays.toString((long[]) request.get(key)));
            } else if (request.get(key) instanceof MeteringRectangle[]) {
                Log.v(TAG, key.getName() + ": " + Arrays.deepToString((MeteringRectangle[]) request.get(key)));
            } else if (request.get(key) instanceof Rational[]) {
                Log.v(TAG, key.getName() + ": " + Arrays.deepToString((Rational[]) request.get(key)));
            } else if (request.get(key) instanceof Face[]) {
                Log.v(TAG, key.getName() + ": " + Arrays.deepToString((Face[]) request.get(key)));
            } else if (request.get(key) instanceof Point[]) {
                Log.v(TAG, key.getName() + ": " + Arrays.deepToString((Point[]) request.get(key)));
            } else {
                Log.v(TAG, key.getName() + ": " + request.get(key));
            }
        }
    }

    /**
     * Dump {@link SCaptureResult} to console log.
     */
    private void dumpCaptureResultToLog(SCaptureResult result) {

        Log.v(TAG, "Dump of SCaptureResult Frame#" + result.getFrameNumber() + " Seq.#" + result.getSequenceId());
        for (SCaptureResult.Key<?> key : result.getKeys()) {
            if (result.get(key) instanceof int[]) {
                Log.v(TAG, key.getName() + ": " + Arrays.toString((int[]) result.get(key)));
            } else if (result.get(key) instanceof float[]) {
                Log.v(TAG, key.getName() + ": " + Arrays.toString((float[]) result.get(key)));
            } else if (result.get(key) instanceof long[]) {
                Log.v(TAG, key.getName() + ": " + Arrays.toString((long[]) result.get(key)));
            } else if (result.get(key) instanceof MeteringRectangle[]) {
                Log.v(TAG, key.getName() + ": " + Arrays.deepToString((MeteringRectangle[]) result.get(key)));
            } else if (result.get(key) instanceof Rational[]) {
                Log.v(TAG, key.getName() + ": " + Arrays.deepToString((Rational[]) result.get(key)));
            } else if (result.get(key) instanceof Face[]) {
                Log.v(TAG, key.getName() + ": " + Arrays.deepToString((Face[]) result.get(key)));
            } else if (result.get(key) instanceof Point[]) {
                Log.v(TAG, key.getName() + ": " + Arrays.deepToString((Point[]) result.get(key)));
            } else if (result.get(key) instanceof Pair) {
                Pair value = (Pair) result.get(key);
                Log.v(TAG, key.getName() + ": (" + value.first + ", " + value.second + ")");
            } else {
                Log.v(TAG, key.getName() + ": " + result.get(key));
            }
        }
    }

    /**
     * Returns required orientation that the jpeg picture needs to be rotated to be displayed upright.
     */
    private int getJpegOrientation() {
        int degrees = mLastOrientation;

        if (mCharacteristics.get(SCameraCharacteristics.LENS_FACING) == SCameraCharacteristics.LENS_FACING_FRONT) {
            degrees = -degrees;
        }

        return (mCharacteristics.get(SCameraCharacteristics.SENSOR_ORIENTATION) + degrees + 360) % 360;
    }

    /**
     * find optimal preview size for given targetRatio
     */
    private Size getOptimalPreviewSize(Size[] sizes, double targetRatio) {
        final double ASPECT_TOLERANCE = 0.001;

        final int MAX_ASPECT_HEIGHT = screenHeight;
        // Count sizes with height <= 1080p to mimic camera1 api behavior.
        int count = 0;
        for (Size s : sizes) {
            if (s.getHeight() <= MAX_ASPECT_HEIGHT) {
                count++;
            }
        }
        ArrayList<Size> camera1Sizes = new ArrayList<>(count);
        // Set array of all sizes with height <= 1080p
        for (Size s : sizes) {
            if (s.getHeight() <= MAX_ASPECT_HEIGHT) {
                camera1Sizes.add(new Size(s.getWidth(), s.getHeight()));
            }
        }
        int optimalIndex = getOptimalPreviewSizeIndex(camera1Sizes, targetRatio, ASPECT_TOLERANCE);
        if (optimalIndex == -1) {
            return null;
        }
        Size optimal = camera1Sizes.get(optimalIndex);
        for (Size s : sizes) {
            if (s.getWidth() == optimal.getWidth() && s.getHeight() == optimal.getHeight()) {
                return s;
            }
        }
        return null;
    }

    /**
     * Returns the index into 'sizes' that is most optimal given the current
     * screen, target aspect ratio and tolerance.
     *
     * @param previewSizes the available preview sizes
     * @param targetRatio the target aspect ratio, typically the aspect ratio of
     *            the picture size
     * @param aspectRatioTolerance the tolerance we allow between the selected
     *            preview size's aspect ratio and the target ratio. If this is
     *            set to 'null', the default value is used.
     * @return The index into 'previewSizes' for the optimal size, or -1, if no
     *         matching size was found.
     */
    public int getOptimalPreviewSizeIndex(
            List<Size> previewSizes, double targetRatio, Double aspectRatioTolerance) {
        if (previewSizes == null) {
            return -1;
        }
        // If no particular aspect ratio tolerance is set, use the default
        // value.
        aspectRatioTolerance = (aspectRatioTolerance == null ? 0.01 : aspectRatioTolerance);

        int optimalSizeIndex = -1;
        double minDiff = Double.MAX_VALUE;
        // Because of bugs of overlay and layout, we sometimes will try to
        // layout the viewfinder in the portrait orientation and thus get the
        // wrong size of preview surface. When we change the preview size, the
        // new overlay will be created before the old one closed, which causes
        // an exception. For now, just get the screen size.
        WindowManager windowManager = getActivity().getWindowManager();
        Point res = new Point();
        windowManager.getDefaultDisplay().getSize(res);
        Size defaultDisplaySize = new Size(res.x, res.y);

        int targetHeight = Math.min(defaultDisplaySize.getWidth(), defaultDisplaySize.getHeight());
        // Try to find an size match aspect ratio and size
        for (int i = 0; i < previewSizes.size(); i++) {
            Size size = previewSizes.get(i);
            double ratio = (double) size.getWidth() / size.getHeight();
            if (Math.abs(ratio - targetRatio) > aspectRatioTolerance) {
                continue;
            }
            double heightDiff = Math.abs(size.getHeight() - targetHeight);
            if (heightDiff < minDiff) {
                optimalSizeIndex = i;
                minDiff = heightDiff;
            } else if (heightDiff == minDiff) {
                // Prefer resolutions smaller-than-display when an equally close
                // larger-than-display resolution is available
                if (size.getHeight() < targetHeight) {
                    optimalSizeIndex = i;
                    minDiff = heightDiff;
                }
            }
        }
        // Cannot find the one match the aspect ratio. This should not happen.
        // Ignore the requirement.
        if (optimalSizeIndex == -1) {
            Log.w(TAG, "No preview size match the aspect ratio. available sizes: " + previewSizes);
            minDiff = Double.MAX_VALUE;
            for (int i = 0; i < previewSizes.size(); i++) {
                Size size = previewSizes.get(i);
                if (Math.abs(size.getHeight() - targetHeight) < minDiff) {
                    optimalSizeIndex = i;
                    minDiff = Math.abs(size.getHeight() - targetHeight);
                }
            }
        }
        return optimalSizeIndex;
    }

    private CAMERA_STATE getState() {
        return mState;
    }

    private synchronized void setState(CAMERA_STATE state) {
        mState = state;
    }

    /**
     * Starts AF process by triggering {@link SCaptureRequest#CONTROL_AF_TRIGGER_START}.
     */
    private void lockAF() {
        try {
            setState(CAMERA_STATE.WAIT_AF);
            isAFTriggered = false;

            // Set AF trigger to SCaptureRequest.Builder
            mPreviewBuilder.set(SCaptureRequest.CONTROL_AF_TRIGGER, SCaptureRequest.CONTROL_AF_TRIGGER_START);

            // App should send AF triggered request for only a single capture.
            mSCameraSession.capture(mPreviewBuilder.build(), new SCameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(SCameraCaptureSession session, SCaptureRequest request, STotalCaptureResult result) {
                    isAFTriggered = true;
                }
            }, mBackgroundHandler);
            mPreviewBuilder.set(SCaptureRequest.CONTROL_AF_TRIGGER, SCaptureRequest.CONTROL_AF_TRIGGER_IDLE);
        } catch (CameraAccessException e) {
            showAlertDialog("Fail to trigger AF", true);
        }
    }

    /**
     * Opens a {@link SCameraDevice}.
     */
    synchronized private void openCamera(int facing) {
        try {
            if (!mCameraOpenCloseLock.tryAcquire(3000, TimeUnit.MILLISECONDS)) {
                showAlertDialog("Time out waiting to lock camera opening.", true);
            }

            mSCameraManager = mSCamera.getSCameraManager();

            mCameraId = null;

            // Find camera device that facing to given facing parameter.
            for (String id : mSCamera.getSCameraManager().getCameraIdList()) {
                SCameraCharacteristics cameraCharacteristics =
                        mSCamera.getSCameraManager().getCameraCharacteristics(id);
                if (cameraCharacteristics.get(SCameraCharacteristics.LENS_FACING) == facing) {
                    mCameraId = id;
                    break;
                }
            }

            if (mCameraId == null) {
                showAlertDialog("No camera exist with given facing: " + facing, true);
                return;
            }

            // acquires camera characteristics
            mCharacteristics = mSCamera.getSCameraManager().getCameraCharacteristics(mCameraId);

            StreamConfigurationMap streamConfigurationMap =
                    mCharacteristics.get(SCameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            // Acquires supported preview size list that supports SurfaceTexture
            mPreviewSize =  getOptimalPreviewSize(streamConfigurationMap.getOutputSizes(
                    SurfaceTexture.class), (double) mPictureSize.getWidth() / mPictureSize.getHeight());

            Log.d(TAG, "Picture Size: " + mPictureSize.toString() +
                    " Preview Size: " + mPreviewSize.toString());

            // Configures an ImageReader
            mJpegReader = ImageReader.newInstance(mPictureSize.getWidth(),
                    mPictureSize.getHeight(), ImageFormat.JPEG, 1);
            mJpegReader.setOnImageAvailableListener(mImageCallback, mImageSavingHandler);

            if (contains(mCharacteristics.get(SCameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES),
                    SCameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW)) {
                List<Size> rawSizeList = new ArrayList<>();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        streamConfigurationMap.getHighResolutionOutputSizes(ImageFormat.RAW_SENSOR) != null) {
                    rawSizeList.addAll(Arrays.asList(streamConfigurationMap.
                            getHighResolutionOutputSizes(ImageFormat.RAW_SENSOR)));
                }
                rawSizeList.addAll(Arrays.asList(
                        streamConfigurationMap.getOutputSizes(ImageFormat.RAW_SENSOR)));

                Size rawSize = rawSizeList.get(0);

                mRawReader = ImageReader.newInstance(
                        rawSize.getWidth(), rawSize.getHeight(), ImageFormat.RAW_SENSOR, 1);
                mRawReader.setOnImageAvailableListener(mImageCallback, mImageSavingHandler);

                mImageFormatList = Arrays.asList(ImageFormat.JPEG, ImageFormat.RAW_SENSOR);
            } else {
                if (mRawReader != null) {
                    mRawReader.close();
                    mRawReader = null;
                }
                mImageFormatList = Collections.singletonList(ImageFormat.JPEG);
            }
            mImageFormat = ImageFormat.JPEG;

            // Set the aspect ratio to TextureView
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTextureView.setAspectRatio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            } else {
                mTextureView.setAspectRatio(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            }

            // Opening the camera device here
            mSCameraManager.openCamera(mCameraId, new SCameraDevice.StateCallback() {
                @Override
                public void onDisconnected(SCameraDevice sCameraDevice) {
                    mCameraOpenCloseLock.release();
                    if (getState() == CAMERA_STATE.CLOSING)
                        return;
                    showAlertDialog("Camera disconnected.", true);
                }

                @Override
                public void onError(SCameraDevice sCameraDevice, int i) {
                    mCameraOpenCloseLock.release();
                    if (getState() == CAMERA_STATE.CLOSING)
                        return;
                    showAlertDialog("Error while camera open.", true);
                }

                public void onOpened(SCameraDevice sCameraDevice) {
                    mCameraOpenCloseLock.release();
                    if (getState() == CAMERA_STATE.CLOSING)
                        return;
                    //mSettingDialog.dismiss();
                    mSCameraDevice = sCameraDevice;
                    createPreviewSession();
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            showAlertDialog("Cannot open the camera.", true);
            Log.e(TAG, "Cannot open the camera.", e);
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    private void setDefaultJpegSize(SCameraManager manager, int facing) {
        try {
            for (String id : manager.getCameraIdList()) {
                SCameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(id);
                if (cameraCharacteristics.get(SCameraCharacteristics.LENS_FACING) == facing) {
                    List<Size> jpegSizeList = new ArrayList<>();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                            cameraCharacteristics.get(SCameraCharacteristics.
                                    SCALER_STREAM_CONFIGURATION_MAP).
                                    getHighResolutionOutputSizes(ImageFormat.JPEG) != null) {
                        jpegSizeList.addAll(Arrays.asList(cameraCharacteristics.get(
                                SCameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).
                                getHighResolutionOutputSizes(ImageFormat.JPEG)));
                    }
                    jpegSizeList.addAll(Arrays.asList(cameraCharacteristics.get(
                            SCameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).
                            getOutputSizes(ImageFormat.JPEG)));
                    mPictureSize = jpegSizeList.get(0);
                }
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "Cannot access the camera.", e);
        }
    }

    /**
     * Enable/Disable an orientation listener.
     */
    private void setOrientationListener(boolean isEnable) {
        if (mOrientationListener == null) {

            mOrientationListener = new OrientationEventListener(getActivity()) {
                @Override
                public void onOrientationChanged(int orientation) {
                    if (orientation == ORIENTATION_UNKNOWN) return;
                    mLastOrientation = (orientation + 45) / 90 * 90;
                }
            };
        }

        if (isEnable) {
            mOrientationListener.enable();
        } else {
            mOrientationListener.disable();
        }
    }

    /**
     * Shows alert dialog.
     */
    private void showAlertDialog(String message, final boolean finishActivity) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Alert")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (finishActivity) getActivity().finish();
                    }
                }).setCancelable(false);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
    }

    /**
     * Starts back ground thread that callback from camera will posted.
     */
    private void startBackgroundThread() {
        mBackgroundHandlerThread = new HandlerThread("Background Thread");
        mBackgroundHandlerThread.start();
        mBackgroundHandler = new Handler(mBackgroundHandlerThread.getLooper());

        mImageSavingHandlerThread = new HandlerThread("Saving Thread");
        mImageSavingHandlerThread.start();
        mImageSavingHandler = new Handler(mImageSavingHandlerThread.getLooper());
    }

    /**
     * Starts a preview.
     */
    synchronized private void startPreview() {
        if (mSCameraSession == null) return;

        try {
            // Starts displaying the preview.
            mSCameraSession.setRepeatingRequest(mPreviewBuilder.build(),
                    mSessionCaptureCallback, mBackgroundHandler);
            setState(CAMERA_STATE.PREVIEW);
        } catch (CameraAccessException e) {
            showAlertDialog("Fail to start preview.", true);
        }
    }

    /**
     * Stops back ground thread.
     */
    private void stopBackgroundThread() {
        if (mBackgroundHandlerThread != null) {
            mBackgroundHandlerThread.quitSafely();
            try {
                mBackgroundHandlerThread.join();
                mBackgroundHandlerThread = null;
                mBackgroundHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (mImageSavingHandlerThread != null) {
            mImageSavingHandlerThread.quitSafely();
            try {
                mImageSavingHandlerThread.join();
                mImageSavingHandlerThread = null;
                mImageSavingHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Take picture.
     */
    private void takePicture() {
        if (getState() == CAMERA_STATE.CLOSING)
            return;

        try {
            // Sets orientation
            mCaptureBuilder.set(SCaptureRequest.JPEG_ORIENTATION, getJpegOrientation());

            if (mImageFormat == ImageFormat.JPEG) mCaptureBuilder.addTarget(mJpegReader.getSurface());
            else mCaptureBuilder.addTarget(mRawReader.getSurface());

            mSCameraSession.capture(mCaptureBuilder.build(), new SCameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(SCameraCaptureSession session, SCaptureRequest request, STotalCaptureResult result) {

                    try {
                        mCaptureResultQueue.put(result);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (getState() == CAMERA_STATE.CLOSING)
                        return;
                    unlockAF();
                }

                @Override
                public void onCaptureFailed(SCameraCaptureSession session, SCaptureRequest request, SCaptureFailure failure) {
                    if (getState() == CAMERA_STATE.CLOSING)
                        return;
                    showAlertDialog("JPEG Capture failed.", false);
                    unlockAF();
                }
            }, mBackgroundHandler);

            if (mImageFormat == ImageFormat.JPEG) mCaptureBuilder.removeTarget(mJpegReader.getSurface());
            else mCaptureBuilder.removeTarget(mRawReader.getSurface());

            setState(CAMERA_STATE.TAKE_PICTURE);
        } catch (CameraAccessException e) {
            showAlertDialog("Fail to start preview.", true);
        }
    }

    /**
     * Starts AE pre-capture
     */
    private void triggerAE() {
        try {
            setState(CAMERA_STATE.WAIT_AE);
            isAETriggered = false;

            mPreviewBuilder.set(SCaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, SCaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);

            // App should send AE triggered request for only a single capture.
            mSCameraSession.capture(mPreviewBuilder.build(), new SCameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(SCameraCaptureSession session, SCaptureRequest request, STotalCaptureResult result) {
                    isAETriggered = true;
                }
            }, mBackgroundHandler);
            mPreviewBuilder.set(SCaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, SCaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_IDLE);
        } catch (CameraAccessException e) {
            showAlertDialog("Fail to trigger AE", true);
        }
    }

    /**
     * Unlock AF.
     */
    private void unlockAF() {
        // If we send TRIGGER_CANCEL. Lens move to its default position. This results in bad user experience.
        if (mPreviewBuilder.get(SCaptureRequest.CONTROL_AF_MODE) == SCaptureRequest.CONTROL_AF_MODE_AUTO ||
                mPreviewBuilder.get(SCaptureRequest.CONTROL_AF_MODE) == SCaptureRequest.CONTROL_AF_MODE_MACRO) {
            setState(CAMERA_STATE.PREVIEW);
            return;
        }

        // Triggers CONTROL_AF_TRIGGER_CANCEL to return to initial AF state.
        try {
            mPreviewBuilder.set(SCaptureRequest.CONTROL_AF_TRIGGER, SCaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
            mSCameraSession.capture(mPreviewBuilder.build(), new SCameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(SCameraCaptureSession session, SCaptureRequest request, STotalCaptureResult result) {
                    if (getState() == CAMERA_STATE.CLOSING)
                        return;
                    setState(CAMERA_STATE.PREVIEW);
                }
            }, mBackgroundHandler);
            mPreviewBuilder.set(SCaptureRequest.CONTROL_AF_TRIGGER, SCaptureRequest.CONTROL_AF_TRIGGER_IDLE);
        } catch (CameraAccessException e) {
            showAlertDialog("Fail to cancel AF", false);
        }
    }

    private enum CAMERA_STATE {
        IDLE, PREVIEW, WAIT_AF, WAIT_AE, TAKE_PICTURE, CLOSING
    }

    /**
     * Saves {@link Image} to file.
     */
    private class ImageSaver {
        void save(final Image image, String filename) {

            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera/");
            if (!dir.exists()) dir.mkdirs();
            final File file = new File(dir, filename);

            if (image.getFormat() == ImageFormat.RAW_SENSOR) {
                SCaptureResult result = null;
                try {
                    result = mCaptureResultQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    final SDngCreator dngCreator = new SDngCreator(mCharacteristics, result);
                    dngCreator.setOrientation(DNG_ORIENTATION.get(getJpegOrientation()));

                    new Handler(Looper.myLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                            byte[] bytes = new byte[buffer.remaining()];
                            buffer.get(bytes);
                            FileOutputStream output = null;
                            try {
                                output = new FileOutputStream(file);
                                dngCreator.writeImage(output, image);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                image.close();
                                dngCreator.close();
                                if (null != output) {
                                    try {
                                        output.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            MediaScannerConnection.scanFile(getActivity(),
                                    new String[]{file.getAbsolutePath()}, null,
                                    new MediaScannerConnection.OnScanCompletedListener() {
                                        public void onScanCompleted(String path, Uri uri) {
                                            Log.i(TAG, "ExternalStorage Scanned " + path + "-> uri=" + uri);
                                        }
                                    });

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(),
                                            "Saved: " + file.getName(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    showAlertDialog("Fail to save DNG file.", false);
                    image.close();
                }
            } else {
                new Handler(Looper.myLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.remaining()];
                        buffer.get(bytes);
                        FileOutputStream output = null;
                        try {
                            output = new FileOutputStream(file);
                            output.write(bytes);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            image.close();
                            if (null != output) {
                                try {
                                    output.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        MediaScannerConnection.scanFile(getActivity(),
                                new String[]{file.getAbsolutePath()}, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.i(TAG, "ExternalStorage Scanned " + path + "-> uri=" + uri);
                                    }
                                });

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Saved: " +
                                        file.getName(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
    }
}