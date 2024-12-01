package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.entity.Sector;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.ArenaService;
import com.example.ticketbookingsystem.utils.ConnectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SectorDao implements DaoCrud<Long, Sector>{
    private final static SectorDao INSTANCE = new SectorDao();
    private final static ArenaService arenaService = ArenaService.getInstance();
    private final static String SAVE_SQL = """
            INSERT INTO sector (sector_name, arena_id)
            VALUES (?, ?)
            """;
    private final static String DELETE_SQL = """
            DELETE FROM sector WHERE id=?
            """;
    private final static String UPDATE_SQL = """
            UPDATE sector
            SET sector_name=?,
                arena_id=?
            WHERE id=?
            """;
    private final static String FIND_ALL_SQL = """
            SELECT s.id, s.sector_name, s.arena_id
            FROM public.sector s
            JOIN public.arena a on a.id = s.arena_id
            """;

    private final static String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE s.id=?
            """;

    private final static String FIND_ALL_BY_ARENA_ID_SQL = FIND_ALL_SQL + """
            WHERE s.arena_id=?
            """;
    public static SectorDao getInstance(){
        return INSTANCE;
    }
    private SectorDao(){
    }
    private Sector buildSector(ResultSet result) throws SQLException {
        return new Sector(
                result.getLong("id"),
                result.getString("sector_name"),
                arenaService.findById((long) result.getInt("arena_id")).get()
        );
    }
    @Override
    public List<Sector> findAll() {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_ALL_SQL)){
            List<Sector> sectorsList = new ArrayList<>();

            var result = statement.executeQuery();
            while (result.next()){
                sectorsList.add(buildSector(result));
            }
            return sectorsList;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }
    @Override
    public Optional<Sector> findById(Long id) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_BY_ID_SQL)){
            statement.setLong(1, id);
            var result = statement.executeQuery();
            Sector sector = null;
            if (result.next()){
                sector = buildSector(result);
            }
            return Optional.ofNullable(sector);
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }
    public List<Sector> findAllByArenaId(Long id){
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(FIND_ALL_BY_ARENA_ID_SQL)){
            List<Sector> sectorList = new ArrayList<>();
            statement.setLong(1, id);
            var result = statement.executeQuery();
            while (result.next()){
                sectorList.add(buildSector(result));
            }
            return sectorList;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }
    private void setStatement(Sector sector, PreparedStatement statement) throws SQLException {
        statement.setString(1, sector.getSectorName());
        statement.setLong(2, sector.getArena().getId());
    }
    @Override
    public Sector save(Sector sector) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)){
            setStatement(sector, statement);

            statement.executeUpdate();
            var keys = statement.getGeneratedKeys();
            if(keys.next()){
                sector.setId(keys.getLong("id"));
            }

            return sector;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }
    @Override
    public boolean update(Sector sector) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(UPDATE_SQL)){
            setStatement(sector, statement);
            statement.setLong(3, sector.getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }
    @Override
    public boolean delete(Long id) {
        try(var connection = ConnectionManager.get();
            var statement = connection.prepareStatement(DELETE_SQL)){
            statement.setLong(1, id);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoCrudException(e);
        }
    }
}
