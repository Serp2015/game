package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }


    @Override
    public Page<Player> findAll(Specification<Player> specification, Pageable pageable) {
        return playerRepository.findAll(specification, pageable);
    }

    @Override
    public List<Player> findAll(Specification<Player> specification) {
        return playerRepository.findAll(specification);
    }

    @Override
    public ResponseEntity<?> createPlayer(Player player) {

        if (player.getName() == null || player.getTitle() == null
                || player.getRace() == null || player.getProfession() == null
                || player.getBirthday() == null || player.getExperience() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (playerValidation(player)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (player.getBanned() == null) player.setBanned(false);

        player.setLevel(getLevel(player));

        player.setUntilNextLevel(getExp(player, getLevel(player)));

        return new ResponseEntity<>(playerRepository.save(player), HttpStatus.OK);
    }

    private Long validateId(String id) {
        try {
            Long idLong = Long.parseLong(id);
            if (idLong <= 0) return null;
            else return idLong;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public ResponseEntity<?> getPlayer(String idString) {
        Long id;
        if ((id = validateId(idString)) == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Optional<Player> o = playerRepository.findById(id);
        if (o.isPresent()) return new ResponseEntity<>(o.get(), HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> updatePlayer(String idString, Player player) {
        Long id;
        if ((id = validateId(idString)) == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (!playerRepository.findById(id).isPresent() || !playerRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            Player updatedPlayer;
            if (player.getName() == null && player.getTitle() == null
                    && player.getRace() == null && player.getProfession() == null
                    && player.getExperience() == null && player.getBirthday() == null
                    && player.getBanned() == null) return getPlayer(idString);
            else {
                updatedPlayer = playerRepository.findById(id).get();
                if (player.getName() != null && (player.getName().length() > 12 || player.getName().length() < 1)
                        || player.getTitle() != null && (player.getTitle().length() > 30 || player.getTitle().length() < 1)
                        || player.getExperience() != null && (player.getExperience() > 10000000 || player.getExperience() < 0)) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                if (player.getBirthday() != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(player.getBirthday());
                    if (calendar.get(Calendar.YEAR) < 2000 || calendar.get(Calendar.YEAR) > 3000) {
                        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                    }
                }

                if (player.getName() != null) updatedPlayer.setName(player.getName());
                if (player.getTitle() != null) updatedPlayer.setTitle(player.getTitle());
                if (player.getRace() != null) updatedPlayer.setRace(player.getRace());
                if (player.getProfession() != null) updatedPlayer.setProfession(player.getProfession());
                if (player.getExperience() != null) updatedPlayer.setExperience(player.getExperience());
                if (player.getBirthday() != null) updatedPlayer.setBirthday(player.getBirthday());
                if (player.getBanned() != null) {
                    updatedPlayer.setBanned(player.getBanned());
                } else updatedPlayer.setBanned(Boolean.FALSE);

                updatedPlayer.setLevel(getLevel(player));

                updatedPlayer.setUntilNextLevel(getExp(player, getLevel(player)));
            }
            return new ResponseEntity<>(playerRepository.save(updatedPlayer), HttpStatus.OK);
        }
    }

    private int getExp(Player player, int level) {
        return 50 * (level + 1) * (level + 2) - player.getExperience();
    }

    private int getLevel(Player player) {
        int level = (int) (Math.sqrt(2500 + (200 * player.getExperience())) - 50) / 100;
        return level;
    }

    private boolean playerValidation(Player player) {

        if (player.getName().length() > 12 || player.getName().length() < 1)
            return true;

        if (player.getTitle().length() > 30 || player.getTitle().length() < 1)
            return true;

        if (player.getExperience() < 0 || player.getExperience() > 10000000)
            return true;

        if (player.getBirthday().getTime() < 0)
            return true;

        if (player.getBirthday() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(player.getBirthday());
            if (calendar.get(Calendar.YEAR) < 2000 || calendar.get(Calendar.YEAR) > 3000) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ResponseEntity<?> deletePlayer(String idString) {
        Long id;
        if ((id = validateId(idString)) == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!playerRepository.existsById(id))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else {
            playerRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @Override
    public long count(Specification<Player> specification) {
        return playerRepository.count(specification);
    }

    @Override
    public Specification<Player> filterByName(String name) {
        return (root, criteriaQuery, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    @Override
    public Specification<Player> filterByTitle(String title) {
        return (root, criteriaQuery, criteriaBuilder) ->
                title == null ? null : criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    @Override
    public Specification<Player> filterByRace(Race race) {
        return (root, criteriaQuery, criteriaBuilder) ->
                race == null ? null : criteriaBuilder.equal(root.get("race"), race);
    }

    @Override
    public Specification<Player> filterByProfession(Profession profession) {
        return (root, criteriaQuery, criteriaBuilder) ->
                profession == null ? null : criteriaBuilder.equal(root.get("profession"), profession);
    }

    @Override
    public Specification<Player> filterByBirthday(Long after, Long before) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (after == null && before == null)
                return null;
            if (after == null) {
                Date beforeCurrent = new Date(before);
                return criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), beforeCurrent);
            }
            if (before == null) {
                Date afterCurrent = new Date(after);
                return criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), afterCurrent);
            }
            Date beforeCurrent = new Date(before);
            Date afterCurrent = new Date(after);
            return criteriaBuilder.between(root.get("birthday"), afterCurrent, beforeCurrent);
        };
    }

    @Override
    public Specification<Player> filterByBanned(Boolean banned) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (banned == null) {
                return null;
            }
            if (banned) {
                return criteriaBuilder.isTrue(root.get("banned"));
            } else
                return criteriaBuilder.isFalse(root.get("banned"));
        };
    }

    @Override
    public Specification<Player> filterByExperience(Integer min, Integer max) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (min == null && max == null) return null;
            if (min == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("experience"), max);
            }
            if (max == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), min);
            }
            return criteriaBuilder.between(root.get("experience"), min, max);
        };
    }

    @Override
    public Specification<Player> filterByLevel(Integer min, Integer max) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            if (min == null && max == null) return null;
            if (min == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("level"), max);
            }
            if (max == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("level"), min);
            }
            return criteriaBuilder.between(root.get("level"), min, max);
        };
    }

}
