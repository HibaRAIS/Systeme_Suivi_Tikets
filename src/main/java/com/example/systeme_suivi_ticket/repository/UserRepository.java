package com.example.systeme_suivi_ticket.repository;

import com.example.systeme_suivi_ticket.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);
	Optional<User> findByUsername(String username); // Changed to Optional and correct method name
	boolean existsByUsername(String username); // Added
	Optional<User> findByEmail(String email); // Added

	long countByActiveTrue();
	long countByActiveFalse();
	long countByRole_RoleName(String roleName);

	@Query("SELECT u FROM User u WHERE "
			+ "(:userType IS NULL OR u.role.id = :userType) "
			+ "AND (:userStatus IS NULL OR u.active = :userStatus) "
			+ "AND (:searchTerm IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
			+ "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
			+ "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) "
			+ "OR LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
	List<User> findUsersByFilters(@Param("userType") Integer userType,
								  @Param("userStatus") Boolean userStatus,
								  @Param("searchTerm") String searchTerm);
	// tickets
	List<User> findByRole_RoleName(String roleName);
	List<User> findTop5ByOrderByCreatedDateDesc();



}
