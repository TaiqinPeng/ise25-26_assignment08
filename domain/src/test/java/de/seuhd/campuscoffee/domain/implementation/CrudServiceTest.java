package de.seuhd.campuscoffee.domain.implementation;

import de.seuhd.campuscoffee.domain.model.objects.DomainModel;
import de.seuhd.campuscoffee.domain.ports.data.CrudDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrudServiceTest {

    @Mock
    private CrudDataService<TestDomain, Long> dataService;

    private TestCrudService crudService;

    @BeforeEach
    void setUp() {
        crudService = new TestCrudService(dataService);
    }

    @Test
    void clear_callsDataServiceClear() {
        crudService.clear();
        verify(dataService).clear();
    }

    @Test
    void delete_callsDataServiceDelete() {
        crudService.delete(5L);
        verify(dataService).delete(5L);
    }

    @Test
    void upsert_create_doesNotCallGetById_beforeUpsert() {
        TestDomain obj = new TestDomain(null);

        when(dataService.upsert(obj)).thenReturn(obj);

        crudService.upsert(obj);

        verify(dataService, never()).getById(anyLong());
        verify(dataService).upsert(obj);
    }

    @Test
    void upsert_update_callsGetById_beforeUpsert() {
        TestDomain obj = new TestDomain(7L);

        when(dataService.getById(7L)).thenReturn(obj);
        when(dataService.upsert(obj)).thenReturn(obj);

        crudService.upsert(obj);

        verify(dataService).getById(7L);
        verify(dataService).upsert(obj);
    }

    // ---- test helper types ----

    static class TestCrudService extends CrudServiceImpl<TestDomain, Long> {
        private final CrudDataService<TestDomain, Long> ds;

        TestCrudService(CrudDataService<TestDomain, Long> ds) {
            super(TestDomain.class);
            this.ds = ds;
        }

        @Override
        protected CrudDataService<TestDomain, Long> dataService() {
            return ds;
        }
    }

    static class TestDomain implements DomainModel<Long> {
        private final Long id;

        TestDomain(Long id) {
            this.id = id;
        }

        @Override
        public Long getId() {
            return id;
        }
    }
}
