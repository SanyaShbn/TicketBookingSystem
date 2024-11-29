package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dao.ArenaDao;
import com.example.ticketbookingsystem.entity.Arena;

import java.util.List;
import java.util.Optional;

public class ArenaService {
    private final static ArenaService INSTANCE = new ArenaService();
    private final ArenaDao arenaDao = ArenaDao.getInstance();
    private ArenaService(){}
    public static ArenaService getInstance(){
        return INSTANCE;
    }

    public List<Arena> findAll(){
        return arenaDao.findAll();
    }

    public Optional<Arena> findById(Long id){
        return arenaDao.findById(id);
    }

    public void createArena(Arena arena) {
        arenaDao.save(arena);
    }

    public void updateArena(Arena arena) {
        arenaDao.update(arena);
    }

    public void deleteArena(Long id) {
        arenaDao.delete(id);
    }
}
