package vesnell.pl.lsportfolio.database.controller;

import android.content.Context;
import android.os.Handler;

import java.util.Collections;
import java.util.List;

import vesnell.pl.lsportfolio.database.controller.base.BaseController;
import vesnell.pl.lsportfolio.database.controller.base.CallbackRunnable;
import vesnell.pl.lsportfolio.database.controller.base.ControllerHandler;
import vesnell.pl.lsportfolio.database.controller.base.ControllerRunnable;
import vesnell.pl.lsportfolio.database.model.Project;

public class ProjectController extends BaseController<Project> {

    public ProjectController(Context context) {
        super(context, Project.class);
    }

    public interface ProjectsListLoadCallback {
        void onProjectsListLoaded(List<Project> projects);
    }
    public interface ProjectLoadCallback {
        void onProjectLoaded(Project project);
    }
    public interface ProjectSaveCallback {
        void onProjectSaved(boolean result, Project project);
    }
    public interface ProjectsListSaveCallback {
        void onProjectsListSaved(boolean result);
    }

    private ProjectControllerHandler handler = new ProjectControllerHandler();

    public void setProjectLoadCallback(ProjectLoadCallback projectLoadCallback) {
        handler.projectLoadCallback = projectLoadCallback;
    }
    public void setProjectsListLoadCallback(ProjectsListLoadCallback projectsListLoadCallback) {
        handler.projectsListLoadCallback = projectsListLoadCallback;
    }
    public void setProjectSaveCallback(ProjectSaveCallback projectSaveCallback) {
        handler.projectSaveCallback = projectSaveCallback;
    }
    public void setProjectsListSaveCallback(ProjectsListSaveCallback projectsListSaveCallback) {
        handler.projectsListSaveCallback = projectsListSaveCallback;
    }

    public void loadProject(final String projectId) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                Project project = get(projectId);
                handler.onProjectLoaded(project);
            }
        });
    }

    public void requestList() {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                List<Project> projects = listAll();
                Collections.sort(projects, Collections.reverseOrder());
                handler.onProjectsListLoaded(projects);
            }
        });
    }

    public void saveProjectsList(final List<Project> projects) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                for (Project project : projects) {
                    String projectId = project.getId();
                    if (get(projectId) == null) {
                        create(project);
                    }
                }
                handler.onProjectsListSaved(true);
            }
        });
    }

    public void updateProject(final Project project) {
        ControllerHandler.getInstance().execute(new ControllerRunnable() {
            @Override
            protected void runController() {
                handler.onProjectSaved(update(project), project);
            }
        });
    }

    private static class ProjectControllerHandler extends Handler implements ProjectsListLoadCallback,
            ProjectsListSaveCallback, ProjectLoadCallback, ProjectSaveCallback {

        private ProjectsListLoadCallback projectsListLoadCallback;
        private ProjectsListSaveCallback projectsListSaveCallback;
        private ProjectSaveCallback projectSaveCallback;
        private ProjectLoadCallback projectLoadCallback;

        @Override
        public void onProjectLoaded(final Project project) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    projectLoadCallback.onProjectLoaded(project);
                }
            });
        }

        @Override
        public void onProjectSaved(final boolean result, final Project project) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    projectSaveCallback.onProjectSaved(result, project);
                }
            });
        }

        @Override
        public void onProjectsListLoaded(final List<Project> projects) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    projectsListLoadCallback.onProjectsListLoaded(projects);
                }
            });
        }

        @Override
        public void onProjectsListSaved(final boolean result) {
            this.post(new CallbackRunnable() {
                @Override
                protected void runCallback() {
                    projectsListSaveCallback.onProjectsListSaved(result);
                }
            });
        }
    }
}
