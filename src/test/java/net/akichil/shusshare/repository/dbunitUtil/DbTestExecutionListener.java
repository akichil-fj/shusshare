package net.akichil.shusshare.repository.dbunitUtil;

import org.springframework.lang.NonNull;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import javax.sql.DataSource;

public class DbTestExecutionListener extends AbstractTestExecutionListener {

    private static final String DATA_PATH = "src/test/resources/testdata/init";

    @Override
    public void beforeTestMethod(@NonNull TestContext testContext) throws Exception {
        super.beforeTestMethod(testContext);
        DataSource dataSource = testContext.getApplicationContext().getBean(DataSource.class);
        DbUnitUtil.loadData(dataSource, DATA_PATH);
    }

    @Override
    public void afterTestMethod(@NonNull TestContext testContext) throws Exception {
        super.afterTestMethod(testContext);
    }
}
