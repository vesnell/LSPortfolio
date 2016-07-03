package vesnell.pl.lsportfolio.database.controller;

import android.content.Context;
import android.os.Handler;

import vesnell.pl.lsportfolio.database.controller.base.BaseController;
import vesnell.pl.lsportfolio.database.controller.base.CallbackRunnable;
import vesnell.pl.lsportfolio.database.controller.base.ControllerHandler;
import vesnell.pl.lsportfolio.database.controller.base.ControllerRunnable;
import vesnell.pl.lsportfolio.database.model.Project;
import vesnell.pl.lsportfolio.database.model.ProjectDetails;

public class ProjectDetailsController extends BaseController<ProjectDetails> {

    public ProjectDetailsController(Context context) {
        super(context, ProjectDetails.class);
    }
    public interface ProjectDetailsLoadCallback {
        void onProjectDetailsLoaded(ProjectDetails projectDetails);
    }
    public interface ProjectDetailsSaveCallback {
        void onProjectDetailsSaved(boolean result, ProjectDetails projectDetails);
    }

    private ProjectDetailsControllerHandler handler = new ProjectDetailsControllerHandler();

    public void setProjectDetailsLoadCallback(ProjectDetailsLoadCallback projectDetailsLoadCallback) {
        handler.projectDetailsLoadCallback = projectDetailsLoadCallback;
    }
    public void setProjectDetailsSaveCallback(ProjectDetailsSaveCallback projectDetailsSaveCallback) {
        handler.projectDetailsSaveCallback = projectDetailsSaveCallback;
    }

    public void loadProjectDetails(final Project project) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                ProjectDetails projectDetails = project.getProjectDetails();
                handler.onProjectDetailsLoaded(projectDetails);
            }
        });
    }

    public void save(final ProjectDetails projectDetails) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                handler.onProjectDetailsSaved(create(projectDetails), projectDetails);
            }
        });
    }

    public void updateProjectDetails(final ProjectDetails projectDetails) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                handler.onProjectDetailsSaved(update(projectDetails), projectDetails);
            }
        });
    }

    private static class ProjectDetailsControllerHandler extends Handler implements
            ProjectDetailsLoadCallback, ProjectDetailsSaveCallback {

        private ProjectDetailsSaveCallback projectDetailsSaveCallback;
        private ProjectDetailsLoadCallback projectDetailsLoadCallback;

        @Override
        public void onProjectDetailsLoaded(final ProjectDetails projectDetails) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    projectDetailsLoadCallback.onProjectDetailsLoaded(projectDetails);
                }
            });
        }

        @Override
        public void onProjectDetailsSaved(final boolean result, final ProjectDetails projectDetails) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    projectDetailsSaveCallback.onProjectDetailsSaved(result, projectDetails);
                }
            });
        }
    }
}
