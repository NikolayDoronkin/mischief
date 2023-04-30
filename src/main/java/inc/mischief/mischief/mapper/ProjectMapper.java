package inc.mischief.mischief.mapper;

import inc.mischief.mischief.domain.Project;
import inc.mischief.mischief.model.request.project.CreateProjectRequest;
import inc.mischief.mischief.model.request.project.UpdateProjectRequest;
import inc.mischief.mischief.model.response.project.ProjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProjectMapper {

	Project convert(CreateProjectRequest request);
	Project convert(UpdateProjectRequest request);
	ProjectResponse convert(Project user);
	List<ProjectResponse> convert(List<Project> users);

	void update(@MappingTarget Project updatedProject, Project userFromRequest);
}
