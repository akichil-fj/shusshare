package net.akichil.shusshare.repository;

import net.akichil.shusshare.ShusshareApplication;
import net.akichil.shusshare.entity.Shussha;
import net.akichil.shusshare.repository.dbunitUtil.DbTestExecutionListener;
import net.akichil.shusshare.repository.dbunitUtil.DbUnitUtil;
import net.akichil.shusshare.repository.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.sql.DataSource;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ShusshareApplication.class)
public class ShusshaRepositoryImplTest {

    @Autowired
    private ShusshaRepository target;

    @Autowired
    private DataSource dataSource;

    @TestExecutionListeners({DbTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
    @Nested
    public class FindTest {

        @Test
        public void testFind() {
            final Integer accountId = 1;
            final LocalDate date = LocalDate.of(2022, 6, 5);

            Shussha findResult = target.find(accountId, date);

            assertEquals(accountId, findResult.getAccountId());
        }

        @Test
        public void testNotFound() {
            final Integer accountId = 1;
            final LocalDate date = LocalDate.of(2022, 6, 1);

            assertThrows(ResourceNotFoundException.class, () -> target.find(accountId, date));
        }

    }

    @TestExecutionListeners({DbTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
    @Nested
    public class InsertTest {

        private static final String INSERT_DATA_PATH = "src/test/resources/testdata/insert";

        @Test
        public void testInsertSuccess() throws Exception {
            final Shussha insertData = new Shussha();
            insertData.setAccountId(2);
            insertData.setDate(LocalDate.of(2022, 6, 7));

            target.add(insertData);

            DbUnitUtil.assertMutateResults(dataSource, "shussha", INSERT_DATA_PATH,
                    "shussha_id", "updated_at");
        }

    }

    @TestExecutionListeners({DbTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
    @Nested
    public class DeleteTest {

        private static final String DELETE_DATA_PATH = "src/test/resources/testdata/delete";

        @Test
        public void testDeleteSuccess() throws Exception {
            final Shussha deleteData = new Shussha();
            deleteData.setShusshaId(6);
            deleteData.setAccountId(2);
            deleteData.setDate(LocalDate.of(2022, 6, 6));

            target.remove(deleteData);

            DbUnitUtil.assertMutateResults(dataSource, "shussha", DELETE_DATA_PATH,
                    "shussha_id", "updated_at");
        }

        @Test
        public void testDeleteFailResourceNotFound() {
            final Shussha deleteData = new Shussha();
            deleteData.setShusshaId(10);
            deleteData.setAccountId(2);
            deleteData.setDate(LocalDate.of(2022, 6, 10));

            assertThrows(ResourceNotFoundException.class, () -> target.remove(deleteData));
        }


    }

}
