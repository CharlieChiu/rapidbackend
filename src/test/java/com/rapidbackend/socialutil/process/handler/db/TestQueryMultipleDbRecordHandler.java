package com.rapidbackend.socialutil.process.handler.db;

//import org.junit.BeforeClass;
//import org.junit.Test;

//import com.rapidbackend.core.BackendRuntimeException;
import com.rapidbackend.core.RapidbackendTestBase;
import com.rapidbackend.core.request.CommandRequest;
import com.rapidbackend.core.request.IntParam;
import com.rapidbackend.core.request.RequestBase;
@Deprecated
public class TestQueryMultipleDbRecordHandler extends RapidbackendTestBase{
    //@BeforeClass
    public static void BeforeClass() throws Exception{
        prepareTest();
    }
    //@Test
    public void testCreateQuery() {
        QueryMultipleDbRecordHandler queryMultipleDbRecordHandler = new QueryMultipleDbRecordHandler();
        CommandRequest request = new RequestBase();
        request.addParam(new IntParam("id",6));
        request.addParam(new IntParam("feedId",8));
        String queryParams = "id,feedId>,";
        queryMultipleDbRecordHandler.setQueryParams(queryParams);
        queryMultipleDbRecordHandler.getQueryPartials();
    }
    //@Test(expected=BackendRuntimeException.class) // test with an undefined commandparam
    public void testCreateQueryFail() {
        QueryMultipleDbRecordHandler queryMultipleDbRecordHandler = new QueryMultipleDbRecordHandler();
        String queryParams = "abcd,feedId>,";
        queryMultipleDbRecordHandler.setQueryParams(queryParams);
        queryMultipleDbRecordHandler.getQueryPartials();
    }
    //@Test(expected=BackendRuntimeException.class) // test with empty query param
    public void testCreateQueryFail_0() {
        QueryMultipleDbRecordHandler queryMultipleDbRecordHandler = new QueryMultipleDbRecordHandler();
        String queryParams = " ";
        queryMultipleDbRecordHandler.setQueryParams(queryParams);
        queryMultipleDbRecordHandler.getQueryPartials();
    }
}
