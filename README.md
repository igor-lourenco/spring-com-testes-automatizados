## Fundamentos de testes automatizados

### Tipos de testes
- `Unitário: `
  Teste feito pelo desenvolvedor, responsável por validar o comportamento de unidades funcionais de código. Nesse contexto, entende-se como unidade funcional qualquer porção de código que através de algum estímulo seja capaz de gerar um comportamento esperado (na prática: métodos de uma classe). Um teste unitário não pode acessar outros componentes ou recursos externos (arquivos, bd, rede, web services, etc.).

- `Integração: `
  Teste focado em verificar se a comunicação entre componentes / módulos da aplicação, e também recursos externos, estão interagindo entre si corretamente.

- `Funcional: `
  É um teste do ponto de vista do usuário, se uma determinada funcionalidade está executando corretamente, produzindo o resultado ou comportamento desejado pelo usuário.

#### Beneficios:

-	Detectar facilmente se mudanças violaram as regras
-	É uma forma de documentação (comportamento e entradas/saídas esperadas)
-	Redução de custos em manutenções, especialmente em fases avançadas
-	Melhora design da solução, pois a aplicação testável precisa ser bem delineada

##
###  TDD - Test Driven Development
É um método de desenvolver software. Consiste em um desenvolvimento guiado pelos testes.

#### Princípios / vantagens:
-	Foco nos requisitos
-	Tende a melhorar o design do código, pois o código deverá ser testável
-	Incrementos no projeto têm menos chance de quebrar a aplicação

#### Processo básico:
1.	Escreva o teste como esperado (naturalmente que ele ainda estará falhando)
2.	Implemente o código necessário para que o teste passe
3.	Refatore o código conforme necessidade

##
### Boas práticas e padrões

#### Nomenclatura de um teste:
-	`<AÇÃO>` should `<EFEITO>` [when `<CENÁRIO>`]

#### Padrão AAA
-	`Arrange:` instancie os objetos necessários
-	`Act:` execute as ações necessárias
-	`Assert:` declare o que deveria acontecer (resultado esperado)

#### Princípio da inversão de dependência (SOLID)
-	Se uma classe A depende de uma instância da classe B, não tem como testar a classe A isoladamente. Na verdade nem seria um teste unitário.
-	A inversão de controle ajuda na testabilidade, e garante o isolamento da unidade a ser testada.

#### Independência / isolamento
-	Um teste não pode depender de outros testes, nem da ordem de execução

#### Cenário único
-	O teste deve ter uma lógica simples, linear
-	O teste deve testar apenas um cenário
-	Não use condicionais e loops

#### Previsibilidade
-	O resultado de um teste deve ser sempre o mesmo para os mesmos dados
-	Não faça o resultado depender de coisas que variam, tais como timestamp atual e valores aleatórios.

##
### Annotations usadas nas classes de teste

| Anotação                                  | Descrição |
|:------------------------------------------| :--- |
| `@SpringBootTest`                         | Carrega o contexto da aplicação (teste de integração) |
| `@SpringBootTest` `@AutoConfigureMockMvc` | Carrega o contexto da aplicação (teste de integração & web) Trata as requisições sem subir o servidor |
| `@WebMvcTest(Classe.class)`               | Carrega o contexto, porém somente da camada web (teste de unidade: controlador) |
| `@ExtendWith(SpringExtension.class)`      | Não carrega o contexto, mas permite usar os recursos do Spring com JUnit (teste de unidade: service/component) |
| `@DataJpaTest`                            | Carrega somente os componentes relacionados ao Spring Data JPA. Cada teste é transacional e dá rollback ao final. (teste de unidade: repository) |

##
### Fixture

Uma forma de organizar melhor o código dos testes e evitar repetições


| JUnit 5 | JUnit 4 | Objetivo |
| :--- | :--- | :--- |
| `@BeforeAll` | `@BeforeClass` | Preparação antes de todos testes da classe (método estático) |
| `@AfterAll` | `@AfterClass` | Preparação depois de todos testes da classe (método estático) |
| `@BeforeEach` | `@Before` | Preparação antes de cada teste da classe |
| `@AfterEach` | `@After` | Preparação depois de cada teste da classe |


---
# Avançando nos testes unitários

## Abordagens de teste

O teste de software busca descobrir sistematicamente diferentes classes de erros com tempo e esforço mínimos.<br>
Os testes são divididos principalmente em dois grupos: caixa branca e caixa preta.

### Caixa branca
* **Acesso interno:** O testador tem acesso total à estrutura interna e ao código-fonte da aplicação.
* **Foco principal:** Garantir que os componentes internos do software estejam concisos e funcionando corretamente.
* **Análise de caminhos:** Avalia os fluxos e caminhos básicos de execução do código para que sejam devidamente testados.
* **Exemplo clássico:** Os testes unitários são o principal exemplo desta categoria.

### Caixa preta
* **Foco nos requisitos:** Baseia-se nas ações que o software deve desempenhar, focando exclusivamente nos requisitos da aplicação.
* **Código ignorado:** O código-fonte não é considerado; o testador avalia como o sistema funciona externamente, não seus elementos constitutivos.
* **Exemplos clássicos:** Os testes de integração e de API são os principais exemplos desta técnica.

![Imagem1.png](images%2FImagem1.png)

Uma das principais vantagens dos testes unitários por exemplo é proteger os recursos já implementados contra quebras durante alterações no código. Proporciona ao desenvolvedor segurança e proteção da aplicação contra bugs.

---

## Principais anotações Mockito

| Anotação       | Descrição |
|:---------------| :--- |
| `@Mock`        | Cria um objeto falso que simula o comportamento de um componente real. Substitui dependências externas do sistema, sendo muito utilizado para injetar classes como `Repository` ou `Service`. |
| `@Spy`         | Encapsula e "espiona" a instância de um objeto real. Delega as chamadas de métodos para o objeto verdadeiro, rastreando suas execuções e parâmetros. Usado para simular métodos da própria classe sob teste ou em sistemas legados. |
| `@InjectMocks` | Instancia automaticamente o objeto que está sendo testado. Injeta de forma automática todas as dependências que foram criadas usando as anotações `@Mock` e `@Spy`. |

### Diferença entre @Mock e @Spy

* **@Mock (Objeto Falso):** Cria uma instância totalmente vazia (mockada). Se você chamar um método dele sem programar o comportamento antes, ele não executará o código real e retornará o valor padrão (como `null` ou `0`).
* **@Spy (Objeto Real):** Cria uma cópia de uma instância real existente. Se você chamar um método dele sem programar nenhum comportamento, ele executará o código verdadeiro do método e retornará o resultado real.
