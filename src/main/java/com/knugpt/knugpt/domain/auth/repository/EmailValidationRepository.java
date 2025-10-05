package com.knugpt.knugpt.domain.auth.repository;

import com.knugpt.knugpt.domain.auth.redis.EmailValidation;
import org.springframework.data.repository.CrudRepository;

public interface EmailValidationRepository extends CrudRepository<EmailValidation, String> {
}
