package com.swivel.qpon.fileuploader.repository;

import com.swivel.qpon.fileuploader.domain.entity.FileManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileManagerRepository extends JpaRepository<FileManager, String> {

    /**
     * This method returns all the file details for a user
     *
     * @param userId   userId
     * @param pageable pageable
     * @return file page
     */
    Page<FileManager> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    /**
     * This method returns files by file type
     *
     * @param userId      userId
     * @param contentType contentType
     * @param pageable    pageable
     * @return file page
     */
    Page<FileManager> findByUserIdAndTypeOrderByCreatedAtDesc(String userId, String contentType, Pageable pageable);

    /**
     * This method returns the sum of all file size
     *
     * @param userId userId
     * @return all file size
     */
    @Query(value = "SELECT SUM(fileSize) FROM file WHERE user_id=?1", nativeQuery = true)
    long calculateTotalSize(String userId);


    /**
     * This method checks the file existance
     *
     * @param fileId fileId
     * @param userId userId
     * @return true/ false
     */
    boolean existsByIdAndUserId(String fileId, String userId);

    /**
     * This method returns a file manager
     *
     * @param fileId fileId
     * @param userId userId
     * @return fileManager
     */
    Optional<FileManager> findByIdAndUserId(String fileId, String userId);

    /**
     * Search file by name and type
     *
     * @param userId      userId
     * @param contentType contentType
     * @param name        name
     * @param pageable    pageable
     * @return Page<FileManager>
     */
    @Query(value = "SELECT * FROM file f WHERE f.user_id=?1 AND f.contentType like %?2% AND f.name LIKE %?3%",
            countQuery = "SELECT COUNT(*) FROM file f WHERE f.user_id=?1 AND f.contentType like %?2% AND f.name LIKE %?3%",
            nativeQuery = true)
    Page<FileManager> findByUserIdAndFileType(String userId, String contentType, String name, Pageable pageable);

}
