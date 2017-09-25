package org.springframework.sdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.sdr.model.Bar;
import org.springframework.sdr.model.BaseEntity;
import org.springframework.sdr.model.Foo;
import org.springframework.sdr.services.SecurityService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class SecurityTest {

    protected static final String ADMIN = "admin";
    protected static final String EDITOR = "editor";
    protected static final String READER = "reader";
    protected static final String PASSWORD = "password";

    protected static final String DESC = "desc";
    protected static final String MOD_DESC = "modified-desc";

    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Autowired
    SecurityService securityService;

    TypeReference<HashMap<String, Object>> mapTypeRef = new TypeReference<HashMap<String, Object>>() {
    };

    @LocalServerPort
    int port;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    ElasticsearchTemplate esTemplate;

    HttpClient httpClient;

    @Before
    public void setup() throws Exception {

        httpClient = HttpClients.createDefault();

        reset(securityService);
        esTemplate.deleteIndex(Foo.class);
        esTemplate.deleteIndex(Bar.class);
        esTemplate.createIndex(Foo.class);
        esTemplate.createIndex(Bar.class);
    }

    /**
     * Admin create and edit own
     *
     * @throws Exception
     */
    protected <T extends BaseEntity> void testAdminCrudOwn(T entity) throws Exception {

        T created = create(ADMIN, entity);
        assertEquals(ADMIN, created.getName());
        assertEquals(DESC, created.getDesc());
        verify(securityService, times(1)).hasEditMinePermission(any(), any());

        created.setDesc(MOD_DESC);
        T updated = update(ADMIN, created);
        assertEquals(MOD_DESC, updated.getDesc());
        verify(securityService, times(1)).hasReadPermission(any());
        verify(securityService, times(2)).hasEditMinePermission(any(), any());

        T gotten = get(ADMIN, updated);
        assertEquals(updated, gotten);
        verify(securityService, times(2)).hasReadPermission(any());

        delete(ADMIN, gotten);
        verify(securityService, times(3)).hasReadPermission(any());
        verify(securityService, times(1)).hasEditMinePermissionId(any(), any());
        verify(securityService, times(3)).hasEditMinePermission(any(), any());

        try {
            get(ADMIN, gotten);
            fail("should have throw an IllegalArgumentException");
        } catch(IllegalArgumentException e){
            // Expected
            verify(securityService, times(4)).hasReadPermission(any());
        }
    }

    /**
     * Admin create and edit others
     *
     * @throws Exception
     */
    public <T extends BaseEntity> void testAdminRudOthers(T entity) throws Exception {

        T created = create(EDITOR, entity);
        assertEquals(EDITOR, created.getName());
        assertEquals(DESC, created.getDesc());
        verify(securityService, times(1)).hasEditMinePermission(any(), any());

        created.setDesc(MOD_DESC);
        T updated = update(ADMIN, created);
        assertEquals(MOD_DESC, updated.getDesc());
        verify(securityService, times(1)).hasReadPermission(any());
        verify(securityService, times(2)).hasEditMinePermission(any(), any());

        T gotten = get(ADMIN, updated);
        assertEquals(updated, gotten);
        verify(securityService, times(2)).hasReadPermission(any());

        delete(ADMIN, gotten);
        verify(securityService, times(3)).hasReadPermission(any());
        verify(securityService, times(1)).hasEditMinePermissionId(any(), any());
        verify(securityService, times(3)).hasEditMinePermission(any(), any());

        try {
            get(ADMIN, gotten);
            fail("should have throw an IllegalArgumentException");
        } catch(IllegalArgumentException e){
            // Expected
            verify(securityService, times(4)).hasReadPermission(any());
        }
    }

    /**
     * Reader create and edit own
     *
     * @throws Exception
     */
    public <T extends BaseEntity> void testReaderCrudOwn(T entity) throws Exception {

        T created = create(READER, entity);
        assertEquals(READER, created.getName());
        assertEquals(DESC, created.getDesc());
        verify(securityService, times(1)).hasEditMinePermission(any(), any());

        created.setDesc(MOD_DESC);
        T updated = update(READER, created);
        assertEquals(MOD_DESC, updated.getDesc());
        verify(securityService, times(1)).hasReadPermission(any());
        verify(securityService, times(2)).hasEditMinePermission(any(), any());

        T gotten = get(READER, updated);
        assertEquals(updated, gotten);
        verify(securityService, times(2)).hasReadPermission(any());

        delete(READER, gotten);
        verify(securityService, times(3)).hasReadPermission(any());
        verify(securityService, times(1)).hasEditMinePermissionId(any(), any());
        verify(securityService, times(3)).hasEditMinePermission(any(), any());

        try {
            get(READER, gotten);
            fail("should have throw an IllegalArgumentException");
        } catch(IllegalArgumentException e){
            // Expected
            verify(securityService, times(4)).hasReadPermission(any());
        }
    }

    /**
     * Reader can not edit others
     *
     * @throws Exception
     */
    public <T extends BaseEntity> void testReaderCantRudOthers(T entity) throws Exception {

        // Create with Editor
        T created = create(EDITOR, entity);
        assertEquals(EDITOR, created.getName());
        assertEquals(DESC, created.getDesc());
        assertEquals(EDITOR, created.getCreatedBy());
        verify(securityService, times(1)).hasEditMinePermission(any(), any());

        // Try to Edit with Reader
        try{
            created.setDesc(MOD_DESC);
            update(READER, created);
            fail("should have throw an IllegalArgumentException");
        } catch (IllegalArgumentException e){
            // Expected
            verify(securityService, times(1)).hasReadPermission(any());
            verify(securityService, times(2)).hasEditMinePermission(any(), any());
        }

        T gotten = get(READER, created);
        assertEquals(EDITOR, gotten.getName());
        assertEquals(DESC, gotten.getDesc());
        verify(securityService, times(2)).hasReadPermission(any());

        // Try to Delete with Reader
        try{
            delete(READER, created);
            fail("should have throw an IllegalArgumentException");
        } catch (IllegalArgumentException e){
            // Expected
            verify(securityService, times(3)).hasReadPermission(any());
            verify(securityService, times(1)).hasEditMinePermissionId(any(), any());
            verify(securityService, times(3)).hasEditMinePermission(any(), any());
        }

        gotten = get(READER, created);
        assertEquals(EDITOR, gotten.getName());
        assertEquals(DESC, gotten.getDesc());
        verify(securityService, times(4)).hasReadPermission(any());
    }


    /************************************/
    /** Util Methods */
    /************************************/

    @SuppressWarnings("unchecked")
    protected <T extends BaseEntity> T create(String username, T entity) throws Exception {
        String url = "http://localhost:" + port + "/" + getContext(entity);
        HttpPost post = new HttpPost(new URI(url));
        commonHeaders(post, username);
        post.setEntity(new StringEntity(toJson(entity)));
        HttpResponse response = httpClient.execute(post);
        assertStatusCode(response, 201);
        T created = (T) readResult(response, entity.getClass());
        post.completed();
        return created;
    }

    @SuppressWarnings("unchecked")
    protected <T extends BaseEntity> T update(String username, T entity) throws Exception {
        String url = "http://localhost:" + port + "/" + getContext(entity) + "/" + entity.getId();
        HttpPut put = new HttpPut(new URI(url));
        commonHeaders(put, username);
        put.setEntity(new StringEntity(toJson(entity)));
        HttpResponse response = httpClient.execute(put);
        assertStatusCode(response, 200);
        T updated = (T) readResult(response, entity.getClass());
        put.completed();
        return updated;
    }

    @SuppressWarnings("unchecked")
    protected <T extends BaseEntity> T get(String username, T entity) throws Exception {
        String url = "http://localhost:" + port + "/" + getContext(entity) + "/" + entity.getId();
        HttpGet get = new HttpGet(new URI(url));
        commonHeaders(get, username);
        HttpResponse response = httpClient.execute(get);
        assertStatusCode(response, 200);
        T gotten = (T) readResult(response, entity.getClass());
        get.completed();
        return gotten;
    }

    public void delete(String username, BaseEntity entity) throws Exception {
        String url = "http://localhost:" + port + "/" + getContext(entity) + "/" + entity.getId();
        HttpDelete delete = new HttpDelete(new URI(url));
        commonHeaders(delete, username);
        HttpResponse response = httpClient.execute(delete);
        EntityUtils.consumeQuietly(response.getEntity());
        assertStatusCode(response, 204);
        delete.completed();
    }

    protected <T extends BaseEntity> T readResult(HttpResponse response, Class<T> type) throws Exception {
        String json = IOUtils.toString(response.getEntity().getContent(), UTF8);
        return fromJson(json, type);
    }

    protected String getContext(BaseEntity entity) {
        return entity.getClass().getSimpleName().toLowerCase() + "s";
    }

    protected void commonHeaders(HttpRequest request, String username) {
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Accept", "application/json");
        String token = new String(Base64.getEncoder().encode((username + ":" + PASSWORD).getBytes(UTF8)), UTF8);
        request.addHeader("Authorization", "Basic " + token);
    }

    protected String toJson(BaseEntity entity) throws Exception {
        return mapper.writeValueAsString(entity);
    }

    @SuppressWarnings("unchecked")
    protected <T extends BaseEntity> T fromJson(String json, Class<T> type) throws Exception {

        Map<String, Object> map = mapper.readValue(json, mapTypeRef);
        map = (Map<String, Object>) map.get("_links");
        map = (Map<String, Object>) map.get("self");
        String url = (String) map.get("href");
        String id = url.substring(url.lastIndexOf("/") + 1);

        T t = mapper.readValue(json, type);
        t.setId(id);
        return t;
    }

    protected void assertStatusCode(HttpResponse response, int expected) {
        int status = response.getStatusLine().getStatusCode();
        Assert.isTrue(expected == status,
                "Unexpected Response Code, expected [" + expected + "] vs actual [" + status + "]");
    }

    protected String getIdFromUrl(HttpResponse response) {
        String location = response.getFirstHeader("Location").getValue();
        return location.substring(location.lastIndexOf("/") + 1);
    }

}