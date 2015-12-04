package com.example.shareholders.db.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "articles")
public class Article {
	@Id(column = "id")
	long id;
	@Column
	long articleId;

	public long getArticleId() {
		return articleId;
	}

	public void setArticleId(long articleid) {
		this.articleId = articleid;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
