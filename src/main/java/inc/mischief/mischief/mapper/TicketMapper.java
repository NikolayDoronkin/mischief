package inc.mischief.mischief.mapper;

import inc.mischief.mischief.domain.Ticket;
import inc.mischief.mischief.model.request.ticket.CreateTicketRequest;
import inc.mischief.mischief.model.request.ticket.UpdateTicketRequest;
import inc.mischief.mischief.model.response.ticket.TicketResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TicketMapper {

	@Mapping(target = "priority", expression = "java(request.getPriorityName().getPriority())")
	Ticket convert(CreateTicketRequest request);

	@Mapping(target = "priority", expression = "java(request.getPriorityName().getPriority())")
	Ticket convert(UpdateTicketRequest request);

	TicketResponse convert(Ticket user);
	List<TicketResponse> convert(List<Ticket> users);

	default PageImpl<TicketResponse> convert(PageImpl<Ticket> tickets) {
		var responses = tickets.stream()
				.map(this::convert)
				.toList();
		return new PageImpl<>(responses, tickets.getPageable(), tickets.getTotalElements());
	}

	@Mapping(target = "reviewerId", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
	@Mapping(target = "assigneeId", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
	@Mapping(target = "relatableFinishedDate", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
	void update(@MappingTarget Ticket updatedProject, Ticket userFromRequest);
}
