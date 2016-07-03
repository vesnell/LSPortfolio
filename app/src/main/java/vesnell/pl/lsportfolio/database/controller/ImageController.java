package vesnell.pl.lsportfolio.database.controller;

import android.content.Context;
import android.os.Handler;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.List;

import vesnell.pl.lsportfolio.database.DBHelper;
import vesnell.pl.lsportfolio.database.controller.base.BaseController;
import vesnell.pl.lsportfolio.database.controller.base.CallbackRunnable;
import vesnell.pl.lsportfolio.database.controller.base.ControllerHandler;
import vesnell.pl.lsportfolio.database.controller.base.ControllerRunnable;
import vesnell.pl.lsportfolio.database.model.Image;
import vesnell.pl.lsportfolio.database.model.ProjectDetails;
import vesnell.pl.lsportfolio.database.model.Store;

public class ImageController extends BaseController<Image> {

    public ImageController(Context context) {
        super(context, Image.class);
    }

    public interface ImagesListLoadCallback {
        void onImagesListLoaded(List<Image> images);
    }
    public interface ImageLoadCallback {
        void onImageLoaded(Image image);
    }
    public interface ImageSaveCallback {
        void onImageSaved(boolean result, Image image);
    }
    public interface ImagesListSaveCallback {
        void onImagesListSaved(boolean result);
    }

    private ImageControllerHandler handler = new ImageControllerHandler();

    public void setImageLoadCallback(ImageLoadCallback imageLoadCallback) {
        handler.imageLoadCallback = imageLoadCallback;
    }
    public void setImagesListLoadCallback(ImagesListLoadCallback imagesListLoadCallback) {
        handler.imagesListLoadCallback = imagesListLoadCallback;
    }
    public void setImageSaveCallback(ImageSaveCallback imageSaveCallback) {
        handler.imageSaveCallback = imageSaveCallback;
    }
    public void setImagesListSaveCallback(ImagesListSaveCallback imagesListSaveCallback) {
        handler.imagesListSaveCallback = imagesListSaveCallback;
    }

    public void requestList(final ProjectDetails projectDetails) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                RuntimeExceptionDao<ProjectDetails, Integer> projectDetailsDao
                        = OpenHelperManager.getHelper(context, DBHelper.class)
                        .getRuntimeExceptionDao(ProjectDetails.class);
                ProjectDetails refreshPD = projectDetailsDao.queryForId(projectDetails.getId());
                List<Image> images = refreshPD.getImages();
                handler.onImagesListLoaded(images);
            }
        });
    }

    public void createImage(final Image image) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                handler.onImageSaved(create(image), image);
            }
        });
    }

    public void saveImagesList(final List<Image> images) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                for (Image image : images) {
                    create(image);
                }
                handler.onImagesListSaved(true);
            }
        });
    }

    private static class ImageControllerHandler extends Handler implements ImagesListLoadCallback,
            ImagesListSaveCallback, ImageLoadCallback, ImageSaveCallback {

        private ImagesListLoadCallback imagesListLoadCallback;
        private ImagesListSaveCallback imagesListSaveCallback;
        private ImageSaveCallback imageSaveCallback;
        private ImageLoadCallback imageLoadCallback;

        @Override
        public void onImageLoaded(final Image image) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    imageLoadCallback.onImageLoaded(image);
                }
            });
        }

        @Override
        public void onImageSaved(final boolean result, final Image image) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    imageSaveCallback.onImageSaved(result, image);
                }
            });
        }

        @Override
        public void onImagesListLoaded(final List<Image> images) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    imagesListLoadCallback.onImagesListLoaded(images);
                }
            });
        }

        @Override
        public void onImagesListSaved(final boolean result) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    imagesListSaveCallback.onImagesListSaved(result);
                }
            });
        }
    }
}
