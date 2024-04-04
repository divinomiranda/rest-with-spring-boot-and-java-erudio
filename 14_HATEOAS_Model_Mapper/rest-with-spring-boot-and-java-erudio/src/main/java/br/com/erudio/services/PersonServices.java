package br.com.erudio.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.erudio.controllers.PersonController;
import br.com.erudio.data.vo.v1.PersonVO;
import br.com.erudio.exceptions.RequiredObjectIsNullException;
import br.com.erudio.exceptions.ResourceNotFoundException;
import br.com.erudio.mapper.Mapper;
import br.com.erudio.model.Person;
import br.com.erudio.repositories.PersonRepository;

@Service
public class PersonServices implements Serializable {

	private static final long serialVersionUID = 7548062575858265960L;
	
	private Logger logger = Logger.getLogger(PersonServices.class.getName());
	
	@Autowired
	private PersonRepository repository;
	
	public List<PersonVO> findAll() {
		
		logger.info("Finding all person!");
		
		var persons = Mapper.parseListObjects(repository.findAll(), PersonVO.class);
		persons
			.stream()
			.forEach(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
		
		return persons;
	}

	public PersonVO findById(Long id) {
		
		logger.info("Finding one person!");
		
		var entity = repository.findById(id).orElseThrow( () -> new ResourceNotFoundException("No records found for this ID!") );
		var vo = Mapper.parseObject(entity, PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		
		return vo;
	}
	
	public PersonVO create(PersonVO person) {
		
		if (Objects.isNull(person)) throw new RequiredObjectIsNullException();
		
		logger.info("Creating one person!");
		
		var entity = Mapper.parseObject(person, Person.class);
		var vo = Mapper.parseObject(repository.save(entity), PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		
		return vo;
	}
	
	public PersonVO update(PersonVO person) {
		
		if (Objects.isNull(person)) throw new RequiredObjectIsNullException();
		
		logger.info("Updating one person!");
		
		var entity = repository.findById(person.getKey()).orElseThrow( () -> new ResourceNotFoundException("No records found for this ID!") );
		
		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());
		
		var vo = Mapper.parseObject(repository.save(entity), PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		
		return vo;
	}
	
	public void delete(Long id) {
		logger.info("Deleting one person!");
		var entity = repository.findById(id).orElseThrow( () -> new ResourceNotFoundException("No records found for this ID!") );
		repository.delete(entity);
	}
	
}
