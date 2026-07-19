package com.expert.testes.services;

import com.expert.testes.DTOs.ProductDTO;
import com.expert.testes.entities.Category;
import com.expert.testes.entities.Product;
import com.expert.testes.projections.ProductProjection;
import com.expert.testes.repositories.CategoryRepository;
import com.expert.testes.repositories.ProductRepository;
import com.expert.testes.services.exceptions.DatabaseException;
import com.expert.testes.services.exceptions.EntidadeNotFoundException;
import com.expert.testes.services.exceptions.NumeroFormatException;
import com.expert.testes.utils.CategoryFactory;
import com.expert.testes.utils.ProductFactory;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.ObjectNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

//@ExtendWith(SpringExtension.class) // Não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component)
@ExtendWith(MockitoExtension.class) // Não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component)
public class ProductServiceTests {

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private long nonExistingCategoryId;
    private long existingCategoryId;

    private Category categoryExisting;   // Category existente
    private Product product;             // Product com lista de Category vazia
    private Product productWithCategory; // Product com lista de Category existente

    private ProductDTO productDTOWithCategoryDTOEmpty;       // ProductDTO com lista de CategoryDTO vazia
    private ProductDTO productDTOWithNonExistingCategoryId;  // ProductDTO com CategoryDTO não existente
    private ProductDTO productDTOWithCategoryDTO;            // ProductDTO com CategoryDTO existente
    private ProductDTO productDTOWithIdNullAndCategoryDTO;   // ProductDTO com Id null e CategoryDTO existente

    private Product productWithCategoryDTONull;   // Product com Id e Category Null
    private ProductDTO productDTOWithCategoryDTONull;   // ProductDTO com Id null e CategoryDTO Null

    private PageImpl<Product> page;
    private Pageable pageable;

    private ProductProjection projection;
    Page<ProductProjection> projectionPage;
    Pageable pageRequest = PageRequest.of(0, 10);

    @InjectMocks // Define o objeto principal que está sendo testado, cria uma instância real dessa classe e injeta automaticamente todos os mocks criados nela
    private ProductService service;

    @Mock // Cria uma simulação, evita conexões reais com o banco de dados e permite programar retornos fictícios para os métodos do repositório.
    private ProductRepository repository;

    @Mock // Cria uma simulação, evita conexões reais com o banco de dados e permite programar retornos fictícios para os métodos do repositório.
    private CategoryRepository categoryRepository;


    @BeforeEach // Preparação antes de cada teste da classe
    void setUp() throws Exception{

//      Os valores não têm nenhum vínculo com o banco de dados, são apenas valores de controle para simulação
        existingId = 1L;
        nonExistingId = 999L;
        dependentId = 2L;
        nonExistingCategoryId = 999L;
        existingCategoryId = 1L;

        categoryExisting = CategoryFactory.createCategory(existingCategoryId);                            // Category existente
        product = ProductFactory.createWithoutCategory();                                                 // Product com lista de Category vazia
        productWithCategory = ProductFactory.createWitCategory(existingId, existingCategoryId); // Product com lista de Category existente

        productDTOWithCategoryDTOEmpty = ProductFactory.createDTOWithoutCategory();                       // ProductDTO com lista de CategoryDTO vazia
        productDTOWithCategoryDTO = ProductFactory.                                                       // ProductDTO com CategoryDTO existente
            createDTOWithCategoryDTO(existingId, existingCategoryId);
        productDTOWithNonExistingCategoryId = ProductFactory                                              // ProductDTO com CategoryDTO não existente
            .createDTOWithCategoryDTO(existingId, nonExistingCategoryId);
        productDTOWithIdNullAndCategoryDTO = ProductFactory                                              // ProductDTO com Id null e CategoryDTO existente
            .createDTOWithCategoryDTO(null, existingCategoryId);

        productWithCategoryDTONull = ProductFactory.createProductWithCategoryDTONull(existingId);
        productDTOWithCategoryDTONull = ProductFactory.createProductDTOWithCategoryDTONull(null);

        page = new PageImpl<>(List.of(product));
        pageable = PageRequest.of(0, 10);

        projection = Mockito.mock(ProductProjection.class);
        projectionPage = new PageImpl<>(
            List.of(projection),
            PageRequest.of(0, 10),
            1
        );
    }



//	Nomenclatura de um teste: <AÇÃO> should <EFEITO> [when <CENÁRIO>]



    @Test  //  <findAllPagedProductProjection> deve <LancarNumberFormatException> [quando <CategoryIdNaoForNumero>]
    public void findAllPagedProductProjectionShouldThrowNumberFormatExceptionWhenCategoryIdNotForNumber() {
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários

//      -> Act: execute as ações necessárias
        NumeroFormatException ex = Assertions.assertThrows(NumeroFormatException.class, () -> {
            service.findAllPagedProductProjection("Phone", "0L", pageRequest);
        });

//      -> Assert: declare o que deveria acontecer
        Assertions.assertTrue(ex.getMessage().contains("Parâmetro categoryId só pode conter números"));

        Mockito.verify( // garante que o método do 'repository.searchProducts' que está dentro do 'service.findAllPagedProductProjection' não tenha usado.
            repository,
            Mockito.never()
        ).searchProducts(List.of(), "Phone", pageRequest);
    }


    @Test  //  <findAll> deve <RetornarListaDeUser> [quando <>]
    public void findAllShouldReturnListOfUser(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.findAll())
            .thenReturn(List.of(product)); // repository.findAll → deve retornar um List de User


//      -> Act: execute as ações necessárias
        List<ProductDTO> result = service.findAll();


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());

        Mockito.verify( // garante que o método 'repository.findAll' que está dentro do 'service.findAll' tenha sido chamado exatamente 1 vez
            repository,Mockito.times(1)
        ).findAll();
    }

    @Test  //  <findAllPaged> deve <RetornarPage> [quando <>]
    public void findAllPagedShouldReturnPage(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.findAll(Mockito.any(Pageable.class))).thenReturn(page); // repository.findAll → deve retornar um Page quando receber qualquer objeto do tipo Pageable


//      -> Act: execute as ações necessárias
        Page<ProductDTO> result = service.findAllPaged(pageable);


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.getTotalElements());

        Mockito.verify( // garante que o método 'repository.findAll' que está dentro do 'service.findAllPaged' tenha sido chamado exatamente 1 vez
            repository,
            Mockito.times(1)
        ).findAll(pageable);
    }


    @Test  //  <findById> deve <RetornarProductDTO> [quando <IdExistir>]
    public void findByIdShouldReturnProductDTOWhenIdExists(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(productWithCategory)); // repository.findById → deve retornar Optional de Product quando id existir


//      -> Act: execute as ações necessárias
        ProductDTO productDTO = service.findById(existingId);


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertNotNull(productDTO);
        Assertions.assertEquals(1, productDTO.id());
        Assertions.assertEquals(productWithCategory.getCategories().size(), productDTO.categoryDTOS().size());


        Mockito.verify( // garante que o método 'repository.findById' que está dentro do 'service.findById' tenha sido chamado exatamente 1 vez
            repository,
            Mockito.times(1)
        ).findById(existingId);
    }


    @Test  //  <findById> deve <LancarEntidadeNotFoundException> [quando <IdNaoExistir>]
    public void findByIdShouldThrowEntidadeNotFoundExceptionWhenIdDoesNotExists(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty()); // repository.findById → deve retornar Optional vazio quando id não existir


//      -> Act: execute as ações necessárias
        EntidadeNotFoundException ex = Assertions.assertThrows(EntidadeNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertEquals("Product não encontrado: " + nonExistingId, ex.getMessage());


        Mockito.verify( // garante que o método 'repository.findById' que está dentro do 'service.findById' tenha sido chamado exatamente 1 vez
            repository,
            Mockito.times(1)
        ).findById(nonExistingId);
    }


    @Test  //  <insert> deve <PersistirObjeto> [quando <IdEhNull>]
    public void insertShouldPersistObjectWhenIdIsNull(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(categoryRepository.getReferenceById(existingCategoryId)).thenReturn(categoryExisting); // categoryRepository.getReferenceById → deve retornar Category quando id existir
        Mockito.when(repository.save(Mockito.any(Product.class))).thenReturn(productWithCategory); // repository.save → deve retornar um Product com Category quando receber qualquer objeto do tipo Product


//      -> Act: execute as ações necessárias
        ProductDTO productDTO = service.insert(productDTOWithIdNullAndCategoryDTO);


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertNotNull(productDTO);
        Assertions.assertEquals(existingId, productDTO.id());


        Mockito.verify( // garante que o método do 'categoryRepository.getReferenceById' que está dentro do 'service.update' foi usado exatamente 1 vez
            categoryRepository,
            Mockito.times(1)
        ).getReferenceById(existingCategoryId);

        Mockito.verify( // garante que o método 'repository.save' que está dentro do 'service.insert' foi usado exatamente 1 vez
            repository,
            Mockito.times(1))
        .save(Mockito.any(Product.class));
    }


    @Test  //  <insert> deve <PersistirProduct> [quando <ProductIdEhNullEListCategoryDTOEhNull>]
    public void insertShouldPersistProductWhenProductIdIsNullAndListCategoryDTOIsNull(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.save(Mockito.any(Product.class)))
            .thenReturn(productWithCategoryDTONull); // repository.save → deve retornar um Product com Category null quando receber qualquer objeto do tipo Product


//      -> Act: execute as ações necessárias
        ProductDTO productDTO = service.insert(productDTOWithCategoryDTONull);


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertNotNull(productDTO);
        Assertions.assertEquals(existingId, productDTO.id());


        Mockito.verify( // garante que o método 'repository.save' que está dentro do 'service.insert' foi usado exatamente 1 vez
            repository,
            Mockito.times(1))
        .save(Mockito.any(Product.class));
    }


    @Test  //  <insert> deve <LancarEntityNotFoundException> [quando <ErroEhGenerico>]
    public void insertShouldThrowEntityNotFoundExceptionWhenErrorIsGeneric(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.doThrow(new EntityNotFoundException("Erro genérico"))
            .when(categoryRepository)  // categoryRepository.getReferenceById → lança EntityNotFoundException quando categoryId não existir (pra cair no throw e)
            .getReferenceById(existingCategoryId);


//      -> Act: execute as ações necessárias
        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            service.insert(productDTOWithIdNullAndCategoryDTO);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertTrue(ex.getMessage().contains("Erro genérico"));


        Mockito.verify( // garante que o método do 'roleRepository.findByAuthority' que está dentro do 'service.insert' foi usado exatamente 1 vez
            categoryRepository,Mockito.times(1)
        ).getReferenceById(existingCategoryId);


        Mockito // garante que o 'repository' que está dentro do 'service.insert' não foi usado além do esperado após a execução completa
            .verifyNoMoreInteractions(repository);
    }

    @Test  //  <insert> deve <LancarEntidadeNotFoundException> [quando <CategoryIdNaoExistir>]
    public void insertShouldThrowEntidadeNotFoundExceptionWhenCategoryIdDoesNotExists(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.doThrow( // categoryRepository.getReferenceById → lança EntityNotFoundException quando categoryId não existir
                new EntityNotFoundException("Erro", new ObjectNotFoundException(nonExistingCategoryId, "Category")))
            .when(categoryRepository).getReferenceById(nonExistingCategoryId);


//      -> Act: execute as ações necessárias
        EntidadeNotFoundException ex = Assertions.assertThrows(EntidadeNotFoundException.class, () -> {
            service.insert(productDTOWithNonExistingCategoryId);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertTrue(ex.getMessage().contains("Category não encontrado: " + nonExistingCategoryId));

        Mockito.verify( // garante que o método do 'categoryRepository.getReferenceById' que está dentro do 'service.update' foi usado exatamente 1 vez
            categoryRepository,
            Mockito.times(1)
        ).getReferenceById(nonExistingCategoryId);

        Mockito.verify( // garante que o método 'repository.save' que está dentro do 'service.insert' não tenha sido chamado
            repository,
            Mockito.never())
        .save(Mockito.any());
    }


    @Test  //  <update> deve <AtualizarEntidade> [quando <IdExistir>]
    public void updateShouldUpdateEntidadeWhenIdExists(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(productWithCategory)); // repository.findById → deve retornar Optional de Product quando id existir
        Mockito.when(categoryRepository.getReferenceById(existingCategoryId)).thenReturn(categoryExisting); // categoryRepository.getReferenceById → deve retornar Category quando id existir


//      -> Act: execute as ações necessárias
        ProductDTO productDTO = service.update(existingId, productDTOWithCategoryDTO);


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertNotNull(productDTO);
        Assertions.assertEquals(existingCategoryId, productDTO.categoryDTOS().get(0).id());

        Mockito.verify( // garante que o método do 'repository.findById' que está dentro do 'service.update' foi usado exatamente 1 vez
            repository,
            Mockito.times(1)
        ).findById(existingId);
//
        Mockito.verify( // garante que o método do 'categoryRepository.getReferenceById' que está dentro do 'service.update' foi usado exatamente 1 vez
            categoryRepository,
            Mockito.times(1)
        ).getReferenceById(existingCategoryId);

    }


    @Test  //  <update> deve <LancarEntityNotFoundException> [quando <ErroEhGenerico>]
    public void updateShouldThrowEntityNotFoundExceptionWhenErrorIsGeneric(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.findById(existingId))
            .thenReturn(Optional.of(productWithCategory)); // repository.findById → deve retornar Optional de Product quando id existir

        Mockito.doThrow(new EntityNotFoundException("Erro genérico"))
            .when(categoryRepository)  // categoryRepository.getReferenceById → lança EntityNotFoundException quando categoryId não existir (pra cair no throw e)
            .getReferenceById(existingCategoryId);


//      -> Act: execute as ações necessárias
        EntityNotFoundException ex = Assertions.assertThrows(EntityNotFoundException.class, () -> {
            service.update(existingId, productDTOWithCategoryDTO);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertTrue(ex.getMessage().contains("Erro genérico"));


        Mockito.verify( // garante que o método do 'repository.findById' que está dentro do 'service.update' foi usado exatamente 1 vez
            repository,Mockito.times(1)
        ).findById(existingId);


        Mockito.verify( // garante que o método do 'categoryRepository.getReferenceById' que está dentro do 'service.update' foi usado exatamente 1 vez
            categoryRepository,Mockito.times(1)
        ).getReferenceById(existingCategoryId);


        Mockito // garante que o 'repository' que está dentro do 'service.update' não foi usado além do esperado após a execução completa
            .verifyNoMoreInteractions(repository);
    }


    @Test  //  <update> deve <LancarEntidadeNotFoundException> [quando <CategoryIdNaoExistir>]
    public void updateShouldThrowEntidadeNotFoundExceptionWhenCategoryIdDoesNotExists(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product)); // repository.findById → deve retornar Optional de Product quando id existir

        Mockito.doThrow( // categoryRepository.getReferenceById → lança EntityNotFoundException quando categoryId não existir
            new EntityNotFoundException("Erro", new ObjectNotFoundException(nonExistingCategoryId, "Category")))
            .when(categoryRepository).getReferenceById(nonExistingCategoryId);


//      -> Act: execute as ações necessárias
        EntidadeNotFoundException ex = Assertions.assertThrows(EntidadeNotFoundException.class, () -> {
            service.update(existingId, productDTOWithNonExistingCategoryId);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Assertions.assertTrue(ex.getMessage().contains("Category não encontrado: " + nonExistingCategoryId));

        Mockito.verify( // garante que o método do 'repository.findById' que está dentro do 'service.update' foi usado exatamente 1 vez
            repository,
            Mockito.times(1)
        ).findById(existingId);

        Mockito.verify( // garante que o método do 'categoryRepository.getReferenceById' que está dentro do 'service.update' foi usado exatamente 1 vez
            categoryRepository,
                Mockito.times(1)
        ).getReferenceById(nonExistingCategoryId);

        Mockito // garante que o 'repository' que está dentro do 'service.update' não foi usado além do esperado após a execução completa
            .verifyNoMoreInteractions(repository);
    }


    @Test  //  <update> deve <LancarEntidadeNotFoundException> [quando <IdNaoExistir>]
    public void updateShouldThrowEntidadeNotFoundExceptionWhenIdDoesNotExists(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty()); // repository.findById → deve retornar Optional vazio quando id não existir


//      -> Act: execute as ações necessárias
        Assertions.assertThrows(EntidadeNotFoundException.class, () -> {
            service.update(nonExistingId, productDTOWithCategoryDTOEmpty);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Mockito.verify( // garante que o método do 'repository.findById' que está dentro do 'service.update' foi usado exatamente 1 vez
            repository,
            Mockito.times(1)
        ).findById(nonExistingId);

        Mockito // garante que o 'repository' que está dentro do 'service.update' não foi usado além do esperado após a execução completa
            .verifyNoMoreInteractions(repository);
    }


    @Test  //  <delete> deve <LancarDatabaseException> [quando <IdEhDependente>]
    public void deleteShouldThrowDatabaseExceptionWhenDependentId(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.existsById(dependentId)).thenReturn(true); // repository.existsById → retorna true quando o id dependente existir
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId); // repository.deleteById → lançe DataIntegrityViolationException quando deletar id dependente

//      -> Act: execute as ações necessárias
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Mockito.verify( // garante que o método do 'repository.existsById' que está dentro do 'service.delete' foi usado exatamente 1 vez
            repository,
                Mockito.times(1)
        ).existsById(dependentId);

        Mockito.verify( // garante que o método do 'repository.deleteById' que está dentro do 'service.delete' foi usado exatamente 1 vez
            repository,
            Mockito.times(1)
        ).deleteById(dependentId);
    }


    @Test  //  <delete> deve <LancarEntidadeNotFoundException> [quando <IdNaoExistir>]
    public void deleteShouldThrowEntidadeNotFoundExceptionWhenIdDoesNotExists(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false); // repository.existsById → retorna false quando o id não existir


//      -> Act: execute as ações necessárias
        Assertions.assertThrows(EntidadeNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Mockito.verify( // garante que o método do 'repository.existsById' que está dentro do 'service.delete' foi usado exatamente 1 vez
            repository,
                Mockito.times(1)
        ).existsById(nonExistingId);

        Mockito.verify( // garante que o método do 'repository.deleteById' que está dentro do 'service.delete' não tenha usado.
            repository,
                Mockito.never()
        ).deleteById(nonExistingId);
    }


    @Test  //  <delete> deve <FazerNada> [quando <IdExistir>]
    public void deleteShouldDoNothingWhenIdExists(){
//      -> Padrão AAA

//   	-> Arrange: instancie os objetos necessários
        Mockito.when(repository.existsById(existingId)).thenReturn(true); // repository.existsById → retorna true quando o id existir
        Mockito.doNothing().when(repository).deleteById(existingId); //  repository.deleteById → não faz nada quando o id existir


//      -> Act: execute as ações necessárias
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });


//      -> Assert: declare o que deveria acontecer (resultado esperado)
        Mockito.verify( // garante que o método do 'repository.deleteById' que está dentro do 'service.delete' foi usado exatamente 1 vez
            repository,
            Mockito.times(1)
        ).deleteById(existingId);
    }
}
