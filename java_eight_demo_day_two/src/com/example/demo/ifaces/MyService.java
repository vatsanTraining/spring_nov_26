package com.example.demo.ifaces;

import java.util.List;

public interface MyService<T> {

	boolean add(T t);
	List<T> findAll();
}
