package com.ht.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ht.model.Book;
import com.ht.service.BookService;

/**
* @author Qiu
* @time  2018年2月28日 下午7:25:27
* 
*/

@Controller
@RequestMapping("book")
public class BookController {
	
	@Autowired
	private BookService bookService;
	
	@RequestMapping("querybooks")
	@ResponseBody
	public Object querybooks() {
		//采集数据
		List<Book> list=bookService.queryBooks();
		return list;
	}
	
	
}
