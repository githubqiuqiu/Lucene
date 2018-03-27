package com.ht.controller;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.ht.model.Book;
import com.ht.service.BookService;

/**
* @author Qiu
* @time  2018年2月28日 下午8:05:36
* 
*/
@Controller
@RequestMapping("indexmanage")
public class IndexManageController {
	
	@Autowired
	private BookService bookService;
	
	/**
	 * 创建索引  增加索引
	 */
	@RequestMapping("createIndex")
	public void createIndex() {
		//1.采集数据
		List<Book> list=bookService.queryBooks();
		
		//2.将采集到的数据 封装到document对象中
		List<Document> doclist=new ArrayList<>();
		Document document;
		
		//循环将采集到的数据  放入document对象中  并且把所有图书的document对象给存在一个保存document对象的list中
		for (Book book : list) {
			
			//创建文档域
			document=new Document();
			
			//创建 文档域中的 Field域
			//Store 如果为yes  表示存储到文档域中  no表示不存储到文档域中  (但是索引域中还有)
			//StringField  不分词  索引  根据store的yes或者no来判断是否存储
			//FloatField   分词 索引 根据store的yes或者no来判断是否存储  数值型使用
			//StoredField  不分词 不索引  但是存储在Field域中
			//TextField  分词 索引 根据store的yes或者no来判断是否存储
			
			//图书id
			//Field id=new TextField("id", book.getId().toString(), Store.YES);
			//修改后   图书id 不分词  索引  存储  使用 StringField
			Field id=new StringField("id", book.getId().toString(), Store.YES);
			 
			//图书名称  分词 索引 存储  使用TextField
			Field bname=new TextField("bname", book.getBname(), Store.YES);
			
			//图书单价 分词  索引 存储 但是是数值类型   所以使用FloatField  把bigdecimal转换成float类型
			BigDecimal prices=new BigDecimal(book.getPrice()+"");
			Float pricess=prices.floatValue();
			Field price=new FloatField("price", pricess, Store.YES);
			//图书封面图片  不分词  不索引  但需要存储  使用 StoreField
			Field pic=new StoredField("pic", book.getPic());
			//图书介绍  分词  索引  但是不存储 使用 TextField
			Field description=new TextField("description", book.getDescription(), Store.NO);
			
			//设置boots值
			if(book.getId()==3) {
				description.setBoost(100f);
			}
			
			//把Field域 添加到文档域document中
			document.add(id);
			document.add(bname);
			document.add(price);
			document.add(pic);
			document.add(description);
			
			//把每一个document对象  放入document对象的list中
			doclist.add(document);			
		}
		
		//创建分词器   标准分词器
		//Analyzer analyzer=new StandardAnalyzer();
		//使用中文分词器
		Analyzer analyzer=new IKAnalyzer();
		
		
		//先创建IndexWriterConfig
		IndexWriterConfig indexwriteconfig=new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
		
		//创建一个文件对象  指定索引库的文件对象   包括索引库的文件地址
		File file=new File("E:\\lucene\\indexfield\\");
		
	
		try {
			//再创建Directory  索引目录流对象
			//此时使用FSDirectory子类   文件系统存储索引
			Directory directory=FSDirectory.open(file);
			
			//创建IndexWrite 写对象  第一个参数是 Directory  第二个参数是 IndexWriterConfig
			IndexWriter writer=new IndexWriter(directory, indexwriteconfig);
			
			//通过IndexWrite 对象 将 document写入到索引库中
			for (Document document2 : doclist) {
				writer.addDocument(document2);
			}
			
			
			//写完之后关闭 write
			writer.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 删除索引
	 */
	@RequestMapping("deleteindex")
	public void deleteindex() {
		
		//创建标准分词器
		Analyzer analyzer=new StandardAnalyzer();
		
		//创建IndexWriterConfig
		IndexWriterConfig config=new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
		
		try {
			
			//创建Dictory
			Directory dictory=FSDirectory.open(new File("E:\\lucene\\indexfield\\"));
			
			//创建IndexWriter
			IndexWriter writer=new IndexWriter(dictory, config);
			
			//terms 是索引库中最小的单位   需要有索引策略的才能删除  把id为1 的document对象删除
			writer.deleteDocuments(new Term("id","1"));
			
			//删除全部  
			//writer.deleteAll();
			
			//关闭writer
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	//修改索引
	@RequestMapping("updateindex")
	public void updateindex() {
		//创建标准分词器
		Analyzer analyzer=new StandardAnalyzer();
		
		//创建IndexWriterConfig
		IndexWriterConfig config=new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
		
		try {
			
			//创建Dictory
			Directory dictory=FSDirectory.open(new File("E:\\lucene\\indexfield\\"));
			
			//创建IndexWriter
			IndexWriter writer=new IndexWriter(dictory, config);
			
			//修改索引
			//第一个参数 : 指定查询条件
			//第二个参数 : 修改之后的对象
			//修改时  如果查询到了值 就覆盖之前的结果   如果没查询到值  就新增
			//修改流程    -->先查询  再删除  再添加
			Document document=new Document();
			document.add(new TextField("bname", "mysql", Store.YES));
			writer.updateDocument(new Term("bname", "spring boot"), document);
			
			//关闭writer
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
}
