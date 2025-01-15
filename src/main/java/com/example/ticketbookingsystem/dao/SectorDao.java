package com.example.ticketbookingsystem.dao;

import com.example.ticketbookingsystem.dto.SectorFilter;
import com.example.ticketbookingsystem.entity.Sector;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.exception.DaoResourceNotFoundException;
import com.example.ticketbookingsystem.utils.FiltrationSqlQueryParameters;
import com.example.ticketbookingsystem.utils.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DAO class for managing {@link Sector} entities.
 */
@Slf4j
public class SectorDao extends AbstractHibernateDao<Sector> {
    private static final SectorDao INSTANCE = new SectorDao();

    private SectorDao() {
        super(Sector.class);
    }

    /**
     * Returns the singleton instance of {@link SectorDao}.
     *
     * @return the singleton instance
     */
    public static SectorDao getInstance() {
        return INSTANCE;
    }

    /**
     * Retrieves all sectors based on the specified filter and arena ID.
     *
     * @param sectorFilter the filter criteria for sectors
     * @param arenaId the ID of the arena
     * @return a list of sectors matching the criteria
     */
    public List<Sector> findAll(SectorFilter sectorFilter, Long arenaId) {
        FiltrationSqlQueryParameters filtrationSqlQueryParameters = buildSqlQuery(sectorFilter, arenaId);
        String hql = filtrationSqlQueryParameters.sql();
        List<Object> parameters = filtrationSqlQueryParameters.parameters();

        return executeFilterQuery(hql, parameters);
    }

    private FiltrationSqlQueryParameters buildSqlQuery(SectorFilter sectorFilter, Long arenaId) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereHql = new ArrayList<>();
        List<String> sortHql = new ArrayList<>();

        whereHql.add("arena.id = :arenaId");
        parameters.add(arenaId);

        if (!sectorFilter.nameSortOrder().isEmpty()) {
            sortHql.add("sectorName " + sectorFilter.nameSortOrder());
        }
        if (!sectorFilter.maxRowsNumbSortOrder().isEmpty()) {
            sortHql.add("maxRowsNumb " + sectorFilter.maxRowsNumbSortOrder());
        }
        if (!sectorFilter.maxSeatsNumbSortOrder().isEmpty()) {
            sortHql.add("maxSeatsNumb " + sectorFilter.maxSeatsNumbSortOrder());
        }

        var orderBy = sortHql.stream().collect(Collectors.joining(
                ", ",
                !sortHql.isEmpty() ? " ORDER BY " : "",
                ""
        ));

        var where = whereHql.stream().collect(Collectors.joining(
                " AND ",
                !whereHql.isEmpty() ? " WHERE " : "",
                ""
        ));

        String hql = "FROM Sector " + where + orderBy;

        return new FiltrationSqlQueryParameters(hql, parameters);
    }

    private List<Sector> executeFilterQuery(String hql, List<Object> parameters) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Sector> query = session.createQuery(hql, Sector.class);

            if (parameters.size() == 1) {
                query.setParameter("arenaId", parameters.get(0));
            } else {
                for (int i = 0; i < parameters.size(); i++) {
                    query.setParameter(i + 1, parameters.get(i));
                }
            }

            return query.list();
        } catch (HibernateException e) {
            throw new DaoCrudException(e);
        }
    }

    /**
     * Retrieves all sectors.
     *
     * @return a list of all sectors
     */
    @Override
    public List<Sector> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Sector> query = session.createQuery("FROM Sector", Sector.class);
            return query.list();
        } catch (HibernateException e) {
            throw new DaoCrudException(e);
        }
    }

    /**
     * Finds a sector by its ID.
     *
     * @param id the ID of the sector
     * @return an {@link Optional} containing the found sector, or empty if not found
     */
    @Override
    public Optional<Sector> findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Sector sector = session.get(Sector.class, id);
            return Optional.ofNullable(sector);
        } catch (HibernateException e) {
            throw new DaoCrudException(e);
        }
    }

    /**
     * Finds all sectors by the specified arena ID.
     *
     * @param arenaId the ID of the arena
     * @return a list of sectors for the specified arena
     */
    public List<Sector> findAllByArenaId(Long arenaId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Sector> query = session.createQuery("FROM Sector WHERE arena.id = :arenaId", Sector.class);
            query.setParameter("arenaId", arenaId);
            return query.list();
        } catch (HibernateException e) {
            throw new DaoCrudException(e);
        }
    }

    /**
     * Saves a sector.
     *
     * @param sector the sector to be saved
     */
    @Override
    public void save(Sector sector) {
        Transaction transaction = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = session.beginTransaction();
            session.save(sector);
            updateArenaAfterSectorSave(session, sector);
            transaction.commit();
            log.info("Sector saved: {}", sector);
        } catch (HibernateException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            log.error("Failed to save sector: {}", sector);
            throw new DaoCrudException(e);
        } finally {
            session.close();
        }
    }

    /**
     * Updates a sector.
     *
     * @param sector the sector to be updated
     */
    @Override
    public void update(Sector sector) {
        Optional<Sector> sectorBeforeUpdate = findById(sector.getId());
        if (sectorBeforeUpdate.isEmpty()) {
            log.error("Failed to find sector: {}", sector);
            throw new DaoResourceNotFoundException("Sector not found");
        }
        Transaction transaction = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = session.beginTransaction();
            updateArenaBeforeSectorUpdate(session, sectorBeforeUpdate.get(), sector);
            session.update(sector);
            transaction.commit();
            log.info("Sector updated: {}", sector);
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Failed to update sector: {}", sector);
            throw new DaoCrudException(e);
        } finally {
            session.close();
        }
    }

    /**
     * Deletes a sector.
     *
     * @param id the ID of the sector to be deleted
     */
    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            transaction = session.beginTransaction();
            Sector sector = session.load(Sector.class, id);
            if (sector != null) {
                updateArenaAfterSectorDelete(session, sector);
                session.delete(sector);
                transaction.commit();
                log.info("Sector {} deleted with given id: {}", sector, id);
            }
            else {
                transaction.rollback();
                log.error("Failed to find sector to be deleted");
            }
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Failed to delete sector");
            throw new DaoCrudException(e);
        } finally {
            session.close();
        }
    }

    private void updateArenaAfterSectorSave(Session session, Sector sector) throws HibernateException{
        Query<?> query = session.createQuery("UPDATE Arena SET generalSeatsNumb = generalSeatsNumb + :seatsNumb "
                + "WHERE id = :arenaId");
        query.setParameter("seatsNumb", sector.getMaxSeatsNumb());
        query.setParameter("arenaId", sector.getArena().getId());
        query.executeUpdate();
    }

    private void updateArenaBeforeSectorUpdate(Session session, Sector sectorBeforeUpdate, Sector sector)
            throws HibernateException {
        Query<?> query = session.createQuery("UPDATE Arena SET generalSeatsNumb = generalSeatsNumb - :oldSeatsNumb + "
                + ":newSeatsNumb WHERE id = :arenaId");
        query.setParameter("oldSeatsNumb", sectorBeforeUpdate.getMaxSeatsNumb());
        query.setParameter("newSeatsNumb", sector.getMaxSeatsNumb());
        query.setParameter("arenaId", sector.getArena().getId());
        query.executeUpdate();
    }

    private void updateArenaAfterSectorDelete(Session session, Sector sector) throws HibernateException{
        Query<?> query = session.createQuery("UPDATE Arena SET generalSeatsNumb = generalSeatsNumb - :seatsNumb "
                + "WHERE id = :arenaId");
        query.setParameter("seatsNumb", sector.getMaxSeatsNumb());
        query.setParameter("arenaId", sector.getArena().getId());
        query.executeUpdate();
    }
}