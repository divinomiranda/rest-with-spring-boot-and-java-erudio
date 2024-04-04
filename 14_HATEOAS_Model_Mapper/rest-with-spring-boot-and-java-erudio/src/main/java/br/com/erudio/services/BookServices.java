package br.com.erudio.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.erudio.controllers.BookController;
import br.com.erudio.data.vo.v1.BookVO;
import br.com.erudio.exceptions.RequiredObjectIsNullException;
import br.com.erudio.exceptions.ResourceNotFoundException;
import br.com.erudio.mapper.Mapper;
import br.com.erudio.model.Book;
import br.com.erudio.repositories.BookRepository;

@Service
public class BookServices implements Serializable{

	private static final long serialVersionUID = -8726501719013161542L;

	private static Logger logger = Logger.getLogger(BookServices.class.getName());
	
	@Autowired
	private BookRepository repository;
	
	public List<BookVO> findByAll(){
		
		logger.info("Find all Book!");
		var books = Mapper.parseListObjects(repository.findAll(), BookVO.class);
		
		books.stream().forEach(p -> p.add(linkTo(methodOn(BookController.class).findById(p.getKey())).withSelfRel()));
		
		return books;
	}
	
	public BookVO findById(Long id) {
		logger.info("Find one Book!");
		var book = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		var vo = Mapper.parseObject(book, BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	public BookVO create(BookVO book) {
		
		if (Objects.isNull(book)) throw new RequiredObjectIsNullException();
		
		logger.info("Creating one Book!");
		
		var entity = repository.save(Mapper.parseObject(book, Book.class));
		var vo = Mapper.parseObject(entity, BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public BookVO update(BookVO book) {
		if(Objects.isNull(book)) throw new RequiredObjectIsNullException();
		
		logger.info("Updating one book!");
		
		var entity = repository.findById(book.getKey()).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		
		entity.setAuthor(book.getAuthor());
		entity.setLaunchDate(book.getLaunchDate());
		entity.setPrice(book.getPrice());
		entity.setTitle(book.getTitle());
		
		var vo = Mapper.parseObject( repository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		
		return vo;
	}
	
	public void delete(Long id) {
		logger.info("deleting one Book!");
		
		var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
		repository.delete(entity);
	}
	
}
