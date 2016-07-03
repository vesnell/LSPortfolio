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
import vesnell.pl.lsportfolio.database.model.ProjectDetails;
import vesnell.pl.lsportfolio.database.model.Store;

public class StoreController extends BaseController<Store> {

    public StoreController(Context context) {
        super(context, Store.class);
    }

    public interface StoresListLoadCallback {
        void onStoresListLoaded(List<Store> stores);
    }
    public interface StoreLoadCallback {
        void onStoreLoaded(Store store);
    }
    public interface StoreSaveCallback {
        void onStoreSaved(boolean result, Store store);
    }
    public interface StoresListSaveCallback {
        void onStoresListSaved(boolean result);
    }

    private StoreControllerHandler handler = new StoreControllerHandler();

    public void setStoreLoadCallback(StoreLoadCallback storeLoadCallback) {
        handler.storeLoadCallback = storeLoadCallback;
    }
    public void setStoresListLoadCallback(StoresListLoadCallback storesListLoadCallback) {
        handler.storesListLoadCallback = storesListLoadCallback;
    }
    public void setStoreSaveCallback(StoreSaveCallback storeSaveCallback) {
        handler.storeSaveCallback = storeSaveCallback;
    }
    public void setStoresListSaveCallback(StoresListSaveCallback storesListSaveCallback) {
        handler.storesListSaveCallback = storesListSaveCallback;
    }

    public void requestList(final ProjectDetails projectDetails) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                RuntimeExceptionDao<ProjectDetails, Integer> projectDetailsDao
                        = OpenHelperManager.getHelper(context, DBHelper.class)
                        .getRuntimeExceptionDao(ProjectDetails.class);
                ProjectDetails refreshPD = projectDetailsDao.queryForId(projectDetails.getId());
                List<Store> stores = refreshPD.getStores();
                handler.onStoresListLoaded(stores);
            }
        });
    }

    public void createStore(final Store store) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                handler.onStoreSaved(create(store), store);
            }
        });
    }

    public void saveStoresList(final List<Store> stores) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                for (Store store : stores) {
                    create(store);
                }
                handler.onStoresListSaved(true);
            }
        });
    }

    private static class StoreControllerHandler extends Handler implements StoresListLoadCallback,
            StoresListSaveCallback, StoreLoadCallback, StoreSaveCallback {

        private StoresListLoadCallback storesListLoadCallback;
        private StoresListSaveCallback storesListSaveCallback;
        private StoreSaveCallback storeSaveCallback;
        private StoreLoadCallback storeLoadCallback;

        @Override
        public void onStoreLoaded(final Store store) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    storeLoadCallback.onStoreLoaded(store);
                }
            });
        }

        @Override
        public void onStoreSaved(final boolean result, final Store store) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    storeSaveCallback.onStoreSaved(result, store);
                }
            });
        }

        @Override
        public void onStoresListLoaded(final List<Store> stores) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    storesListLoadCallback.onStoresListLoaded(stores);
                }
            });
        }

        @Override
        public void onStoresListSaved(final boolean result) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    storesListSaveCallback.onStoresListSaved(result);
                }
            });
        }
    }
}
