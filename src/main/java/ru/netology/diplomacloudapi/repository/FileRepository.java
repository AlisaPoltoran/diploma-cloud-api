package ru.netology.diplomacloudapi.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.diplomacloudapi.entity.FileEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Integer> {
    Optional<FileEntity> findByName(String fileName);

    boolean existsByName(String fileName);

    List<FileEntity> findAllByUserId (int userId, Pageable pageable);

}
