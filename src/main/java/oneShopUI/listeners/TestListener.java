package oneShopUI.listeners;

import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.TestResult;
import io.qameta.allure.model.TestResultContainer;
import io.qameta.allure.testng.AllureTestNg;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TestListener extends AllureTestNg implements ITestListener {

    private static final ConcurrentMap<Long, AllureLifecycle> allureInstances = new ConcurrentHashMap<>();
    private static final ThreadLocal<String> testUuids = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());
    private static final ThreadLocal<String> containerUuids = ThreadLocal.withInitial(() -> UUID.randomUUID().toString());

    public static AllureLifecycle getAllureInstance() {
        return allureInstances.computeIfAbsent(Thread.currentThread().getId(), k -> io.qameta.allure.Allure.getLifecycle());
    }

    @Override
    public void onTestStart(ITestResult result) {
        String uuid = testUuids.get();
        TestResult testResult = new TestResult()
                .setUuid(uuid)
                .setName(result.getMethod().getMethodName())
                .setFullName(result.getTestClass().getName() + "." + result.getMethod().getMethodName())
                .setStatus(Status.PASSED); // Initializing status
        getAllureInstance().scheduleTestCase(testResult);
        getAllureInstance().startTestCase(uuid);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String uuid = testUuids.get();
        getAllureInstance().updateTestCase(uuid, testResult -> testResult.setStatus(Status.PASSED));
        getAllureInstance().stopTestCase(uuid);
        getAllureInstance().writeTestCase(uuid);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String uuid = testUuids.get();
        getAllureInstance().updateTestCase(uuid, testResult -> testResult.setStatus(Status.FAILED));
        getAllureInstance().stopTestCase(uuid);
        getAllureInstance().writeTestCase(uuid);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String uuid = testUuids.get();
        getAllureInstance().updateTestCase(uuid, testResult -> testResult.setStatus(Status.SKIPPED));
        getAllureInstance().stopTestCase(uuid);
        getAllureInstance().writeTestCase(uuid);
    }

    @Override
    public void onStart(ITestContext context) {
        String containerUuid = containerUuids.get();
        TestResultContainer container = new TestResultContainer()
                .setUuid(containerUuid)
                .setName(context.getName());
        getAllureInstance().startTestContainer(container);
    }

    @Override
    public void onFinish(ITestContext context) {
        String containerUuid = containerUuids.get();
        getAllureInstance().stopTestContainer(containerUuid);
        getAllureInstance().writeTestContainer(containerUuid);
        testUuids.remove();
        containerUuids.remove();
    }
}
