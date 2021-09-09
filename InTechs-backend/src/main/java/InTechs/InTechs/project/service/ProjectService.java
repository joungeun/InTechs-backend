package InTechs.InTechs.project.service;

import InTechs.InTechs.project.value.Image;
import InTechs.InTechs.project.entity.Project;
import InTechs.InTechs.user.entity.User;
import InTechs.InTechs.exception.exceptions.ProjectNotFoundException;
import InTechs.InTechs.exception.exceptions.UserNotFoundException;
import InTechs.InTechs.project.payload.response.ProjectUserResponse;
import InTechs.InTechs.project.repository.ProjectRepository;
import InTechs.InTechs.user.repository.UserRepository;
import InTechs.InTechs.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final FileService fileUploadService;
    private final UserRepository userRepository;

    public void projectInfoChange(int projectId, String name,MultipartFile image){
        if(!name.isBlank()) changeProjectName(projectId,name);
        if(!image.isEmpty()) changeProjectImage(projectId, image);
    }

    private void changeProjectImage(int projectId, MultipartFile image){
        String folder = "/project";
        Project project = projectRepository.findById(projectId).orElseThrow(ProjectNotFoundException::new);
        Image imageInfo = fileUploadService.imageResizeAndUpload(image, folder);
        fileUploadService.fileDelete(folder, project.getImage().getOriName()); // s3에 저장되어있는 파일 삭제
        projectRepository.projectUpdate(projectId, imageInfo);
    }

    private void changeProjectName(int projectId, String name){
        projectRepository.projectUpdate(projectId,name);
    }

    public  void projectDelete(int projectId){
        String folder = "/project";
        Project project = projectRepository.findById(projectId).orElseThrow(ProjectNotFoundException::new); // query dsl로 ㅂ꾸기
        fileUploadService.fileDelete(folder, project.getImage().getOriName());
        projectRepository.delete(project);
    }

    public void projectJoin(int projectId, String email){
        Project project = projectRepository.findById(projectId).orElseThrow(ProjectNotFoundException::new);
        project.addUser(findUserFromEmail(email));
        projectRepository.save(project);
    }

    private User findUserFromEmail(String email){
        return userRepository.findById(email).orElseThrow(UserNotFoundException::new);
    }

    public void projectExit(int projectId, String email){
        Project project = projectRepository.findById(projectId).orElseThrow(ProjectNotFoundException::new);
        project.removeUser(findUserFromEmail(email));
        projectRepository.save(project);
    }

    public List<ProjectUserResponse> projectUserList(int projectId){
        Project project = projectRepository.findById(projectId).orElseThrow(ProjectNotFoundException::new);
        List<User> users = project.getUsers();
        List<ProjectUserResponse> userListResponse = new ArrayList<>();
        for(User user :users){
            ProjectUserResponse userResponse =
                    ProjectUserResponse.builder()
                            .email(user.getEmail())
                            .name(user.getName())
                            .imageUri(user.getImage())
                            .isActive(user.isActive()).build();
            userListResponse.add(userResponse);
        }
        return userListResponse;
    }
}
