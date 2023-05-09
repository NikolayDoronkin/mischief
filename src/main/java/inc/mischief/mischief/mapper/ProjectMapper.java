package inc.mischief.mischief.mapper;

import inc.mischief.mischief.domain.Project;
import inc.mischief.mischief.model.request.project.CreateProjectRequest;
import inc.mischief.mischief.model.request.project.UpdateProjectRequest;
import inc.mischief.mischief.model.response.project.ProjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProjectMapper {

	Project convert(CreateProjectRequest request);

	Project convert(UpdateProjectRequest request);

	ProjectResponse convert(Project user);

	List<ProjectResponse> convert(List<Project> users);

	default PageImpl<ProjectResponse> convert(PageImpl<Project> projects) {
		var responses = projects.stream()
								.map(this::convert)
								.toList();
		return new PageImpl<>(responses, projects.getPageable(), projects.getTotalElements());
	}

	void update(@MappingTarget Project updatedProject, Project userFromRequest);
}
