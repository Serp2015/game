package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PlayerService {

    Page<Player> findAll(Specification<Player> specification, Pageable pageable);
    List<Player> findAll(Specification<Player> specification);
    ResponseEntity<?> createPlayer(Player player);
    ResponseEntity<?> getPlayer(String id);
    ResponseEntity<?> updatePlayer(String id, Player player);
    ResponseEntity<?> deletePlayer(String id);
    long count(Specification<Player> specification);
    Specification<Player> filterByName(String name);
    Specification<Player> filterByTitle(String title);
    Specification<Player> filterByRace(Race race);
    Specification<Player> filterByProfession(Profession profession);
    Specification<Player> filterByBirthday(Long after, Long before);
    Specification<Player> filterByBanned(Boolean banned);
    Specification<Player> filterByExperience(Integer min, Integer max);
    Specification<Player> filterByLevel(Integer min, Integer max);

}
