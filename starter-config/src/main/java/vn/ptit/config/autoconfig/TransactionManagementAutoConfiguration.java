package vn.ptit.config.autoconfig;

import vn.ptit.model.constants.ActivateStatus;
import vn.ptit.model.constants.BasePropertiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Used to manage transactions with the database. Uses Hibernate/JPA as the ORM (Object-Relational Mapping) tool <p>
 * TransactionManagement: Defines and provides the tools needed to process transactions such as:
 * <ul>
 *     <li>Starting and ending transactions</li>
 *     <li>Managing rollbacks (on errors) and commits (on success)</li>
 * </ul>
 * Customizing transaction parameters: Such as isolation level and propagation behavior <p>
 * Integrating Spring Transaction Management with JPA using {@link JpaTransactionManager}
 *
 * @author thoaidc
 */
@AutoConfiguration
@AutoConfigureAfter({DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@ConditionalOnProperty(name = BasePropertiesConstants.ENABLED_DATASOURCE, havingValue = ActivateStatus.ENABLED_VALUE)
@EnableTransactionManagement // Enable annotations-based transaction management (@Transactional) in Spring application
public class TransactionManagementAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(TransactionManagementAutoConfiguration.class);

    /**
     * Provides a programmatic API to manage transactions explicitly (if not use @{@link Transactional})
     * @return A custom {@link TransactionTemplate}
     */
    @Bean
    @ConditionalOnMissingBean(TransactionTemplate.class)
    public TransactionTemplate defaultTransactionTemplate(PlatformTransactionManager platformTransactionManager) {
        log.debug("[TRANSACTION_TEMPLATE_AUTO_CONFIG] - Use default transaction template for manual transaction handle");
        TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        // Set the transaction isolation level (e.g. ISOLATION_READ_COMMITTED prevents reading uncommitted data)
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        // Set transaction behavior (e.g. here ensures new transactions will join existing transactions, if any)
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return transactionTemplate;
    }
}
