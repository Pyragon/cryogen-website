package com.cryo.comments;

import java.util.ArrayList;
import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CommentList {
	
	private int listId;
	private int rightsReq;
	private HashMap<Integer, Comment> comments;
	
}
