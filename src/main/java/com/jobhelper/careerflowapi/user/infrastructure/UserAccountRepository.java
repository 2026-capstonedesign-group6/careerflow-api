package com.jobhelper.careerflowapi.user.infrastructure;

import com.jobhelper.careerflowapi.user.domain.enums.Provider;
import com.jobhelper.careerflowapi.user.domain.entity.User;
import com.jobhelper.careerflowapi.user.domain.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUserAndProvider(User user, Provider provider);

    Optional<UserAccount> findByProviderAndProviderId(Provider provider, String providerId);
}
