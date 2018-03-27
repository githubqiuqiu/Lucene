package com.ht.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
/**
* @author Qiu
* @time  2018年3月1日 下午9:55:01
* 
*/

@Controller
@RequestMapping("indexsearch")
public class IndexSearchController {
	/**
	 * 
	 * 创建搜索对象
	 */
	
	//封装方法
	private void doSearch(Query query) {
		//通过QueryParser 来创建query对象  --->常用    还可以通过Query子类来创建query对象 (不常用)
		//参数  输入lucene的查询语句
		try {
			//2. 创建IndexSearcher
			// 指定索引库的地址
			File indexfile=new File("E:\\lucene\\indexfield\\");
			//创建Directory 索引目录流对象
			Directory directory = FSDirectory.open(indexfile);
			//创建IndexReader 索引读取对象
			IndexReader reader = DirectoryReader.open(directory);
			//创建IndexSearcher 索引搜索对象
			IndexSearcher indexSearcher=new IndexSearcher(reader);
			
			
			// 通过searcher来搜索索引库  返回一个 TopDocs对象
			// 第二个参数：指定需要显示的顶部记录的N条
			TopDocs topdocs=indexSearcher.search(query, 10);
			
			// 根据查询条件匹配出的记录总数
			int count=topdocs.totalHits;
			System.out.println("匹配的总数据条数   "+count);
			
			// 根据查询条件匹配出的记录   已经经过打分的文档
			ScoreDoc[] scoreDocs = topdocs.scoreDocs;
			
			//循环所有的记录信息
			for (ScoreDoc scoredoc : scoreDocs) {
				//获取文档id
				int id=scoredoc.doc;
				
				//通过文档id 获取文档    需要通过索引对象来查询
				Document doc = indexSearcher.doc(id);
				
				System.out.println("商品ID：" + doc.get("id"));
				System.out.println("商品名称：" + doc.get("bname"));
				System.out.println("商品价格：" + doc.get("price"));
				System.out.println("商品图片地址：" + doc.get("pic"));
				System.out.println("==========================");
			}
			
			// 关闭资源
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 使用QueryParser 来创建query对象
	 */
	@RequestMapping("QueryParser")
	public void QueryParser() {
		//1. 创建query对象
		// 使用QueryParser搜索时，需要指定分词器，搜索时的分词器要和索引时的分词器一致
		// 第一个参数：默认搜索的域的名称   第二个参数是 分词器  StandardAnalyzer是标准分词器
		QueryParser parser = new QueryParser("description",new StandardAnalyzer());
				
		try {
			Query query=parser.parse("description: 这  AND 本");
			
			//封装的方法
			doSearch(query);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 使用TermQuery  创建query对象
	 */
	@RequestMapping("TermQuery")
	public void TermQuery() {
		Query query=new TermQuery(new Term("bname","spring"));
		doSearch(query);
	}
	
	/**
	 * 使用 NumericRangeQuery  数字范围查询  数字范围推荐使用
	 */
	@RequestMapping("NumericRangeQuery")
	public void NumericRangeQuery() {
		//参数: 域的名称 最小值 最大值 是否包含最小值 是否包含最大值
		Query query=NumericRangeQuery.newFloatRange("price", 45f, 67f, true, true);
		doSearch(query);
	}
	
	
	/**
	 *使用 BooleanQuery  组合查询
	 */
	@RequestMapping("BooleanQuery")
	public void BooleanQuery() {
		//创建BooleanQuery 
		BooleanQuery booleanquery=new BooleanQuery();
		
		//创建TermQuery对象
		Query query=new TermQuery(new Term("bname","spring"));
		
		//创建NumericRangeQuery对象
		Query query1=NumericRangeQuery.newFloatRange("price", 45f, 67f, true, true);
		
		//1、MUST和MUST表示“与”的关系，即“并集”。 
	    //2、MUST和MUST_NOT前者包含后者不包含。 
	    //3、MUST_NOT和MUST_NOT没意义 
	    //4、SHOULD与MUST表示MUST，SHOULD失去意义； 
	    //5、SHOUlD与MUST_NOT相当于MUST与MUST_NOT。 
	    //6、SHOULD与SHOULD表示“或”的概念。
		
		booleanquery.add(query,Occur.MUST);
		booleanquery.add(query1,Occur.MUST);
		
		doSearch(booleanquery);
	}
	
	/**
	 * 使用 MultiFieldQueryParser  多域查询
	 */
	@RequestMapping("MultiFieldQueryParser")
	public void MultiFieldQueryParser() {
		//创建 MultiFieldQueryParser
		//默认搜索的多个域名
		String name[]= {"bname","description"};
		//创建分词器
		Analyzer analyzer=new StandardAnalyzer();
		
		//创建多域查询
		//还可以在搜索时设置boots值
		Map<String, Float> map=new HashMap<>();
		map.put("bname", 150f);
		MultiFieldQueryParser parser=new MultiFieldQueryParser(name, analyzer,map);
		
		
		try {
			
			//可以写查询语句
			//相当于 bname:lucene OR description:lucene
			
			// +表示 and
			// 空 表示 or
			// -表示not
			Query query=parser.parse("lucene");
			
			doSearch(query);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
