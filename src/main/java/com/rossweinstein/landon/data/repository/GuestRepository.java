package com.rossweinstein.landon.data.repository;

import com.rossweinstein.landon.data.entity.Guest;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GuestRepository extends PagingAndSortingRepository<Guest, Long> {

}
