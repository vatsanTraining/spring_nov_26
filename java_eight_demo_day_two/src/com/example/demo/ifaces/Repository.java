package com.example.demo.ifaces;

import java.util.List;

public interface Repository<T> {

	boolean add(T t);
	List<T> findAll();
}
