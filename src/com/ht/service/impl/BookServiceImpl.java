package com.ht.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ht.mapper.BookMapper;
import com.ht.model.Book;
import com.ht.service.BookService;

/**
* @author Qiu
* @time  2018年2月28日 下午7:24:30
* 
*/

@Service
@Transactional
public class BookServiceImpl implements BookService{

	@Autowired
	private BookMapper bookMapper;
	
	@Override
	public List<Book> queryBooks() {
		return bookMapper.queryBooks();
	}

}
