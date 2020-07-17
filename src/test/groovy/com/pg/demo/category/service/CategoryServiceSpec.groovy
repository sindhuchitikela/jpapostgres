package com.pg.demo.category.service

import com.pg.demo.category.dataobject.Category
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class CategoryServiceSpec extends Specification {
    @Subject
    CategoryService categoryService

    @Shared
    def imageClient

    @Shared
    def categoryObjectTransformer

    @Shared
    def categoryRepository;

    @Shared
    def mockCategoryService;

    @Shared
    def offerClient;

    def setup() {
        categoryRepository = Mock(com.pg.demo.category.dao.CategoryRepository)
        offerClient = Mock(OfferClient)
        imageClient = Mock(ImageClient)
        mockCategoryService = Mock(CategoryService)
        categoryService = new CategoryServiceImpl(categoryObjectTransformer, categoryRepository, offerClient)
    }

    @Unroll
    def "Can fetch list of categories by ids"() {
        given:
        def rootCategory = new com.pg.demo.category.dataobject.Category();
        rootCategory.setId(0L)
        rootCategory.setName("ParentRoot")
        rootCategory.setStatus(com.pg.demo.category.enu.CategoryStatus.ACTIVE)

        def category1 = new com.pg.demo.category.dataobject.Category();
        category1.setId(1L)
        category1.setName("TestCat#1")
        category1.setStatus(com.pg.demo.category.enu.CategoryStatus.ACTIVE)
        category1.setParentCategory(rootCategory)

        def category2 = new com.pg.demo.category.dataobject.Category();
        category2.setId(2L)
        category2.setName("TestCat#2")
        category2.setStatus(com.pg.demo.category.enu.CategoryStatus.ACTIVE)
        category2.setParentCategory(rootCategory)


        def category3 = new com.pg.demo.category.dataobject.Category();
        category3.setId(3L)
        category3.setName("TestCat#3")
        category3.setStatus(com.pg.demo.category.enu.CategoryStatus.INACTIVE)
        category3.setParentCategory(rootCategory)

        def categories = [category1, category2, category3]
        rootCategory.setChildCategories(categories)
        when:
        def success = categoryService.fetchCategories(ids, fetchInActive, fetchParents)

        then:
        1 * categoryRepository.findAllById(ids) >> categories
        categoryObjectTransformer.copyFromDataObject(category1) >> fetchDTO(category1)
        categoryObjectTransformer.copyFromDataObject(category2) >> fetchDTO(category2)
        categoryObjectTransformer.copyFromDataObject(category3) >> fetchDTO(category3)

        success.size() == count

        where:
        ids          | fetchInActive | fetchParents | count
        [1L, 2L, 3L] | true          | false        | 3
        [1L, 2L, 3L] | false         | false        | 2
    }

    @Unroll
    def "Can fetch list of categories by ids - with fetchParents option true"() {
        given:
        def rootCategory = new com.pg.demo.category.dataobject.Category();
        rootCategory.setId(0L)
        rootCategory.setName("ParentRoot")
        rootCategory.setStatus(com.pg.demo.category.enu.CategoryStatus.ACTIVE)

        def category1 = new com.pg.demo.category.dataobject.Category();
        category1.setId(1L)
        category1.setName("Apparel")
        category1.setStatus(com.pg.demo.category.enu.CategoryStatus.ACTIVE)
        category1.setParentCategory(rootCategory)
        category1.setParentId(0L)

        def category2 = new com.pg.demo.category.dataobject.Category();
        category2.setId(2L)
        category2.setName("Food")
        category2.setStatus(com.pg.demo.category.enu.CategoryStatus.ACTIVE)
        category2.setParentCategory(rootCategory)
        category1.setParentId(0L)

        def category3 = new com.pg.demo.category.dataobject.Category();
        category3.setId(3L)
        category3.setName("Beverages")
        category3.setStatus(com.pg.demo.category.enu.CategoryStatus.INACTIVE)
        category3.setParentCategory(rootCategory)
        category1.setParentId(0L)

        def category4 = new com.pg.demo.category.dataobject.Category();
        category4.setId(4L)
        category4.setName("Baby Apparel")
        category4.setStatus(com.pg.demo.category.enu.CategoryStatus.INACTIVE)
        category4.setParentCategory(category1)
        category4.setParentId(1L)

        category1.setChildCategories([category4])
        rootCategory.setChildCategories([category1, category2, category3])

        when:
        def success = categoryService.fetchCategories(ids, true, true)

        then:
        1 * categoryRepository.findAllById(ids) >> [category1, category2, category4]
        1 * categoryRepository.findById(1L) >> Optional.of(category1)
        3 * categoryRepository.findById(0L) >> Optional.of(rootCategory)

        noExceptionThrown()

        where:
        ids          | fetchInActive | fetchParents
        [1L, 2L, 4L] | true          | true
    }

    @Unroll
    def "Fetch by list of one category id - with fetchParents option true, deep assertion"() {
        given:
        //ParentRoot >> Apparel >> Baby >> Baby Girl
        def rootCategory = new com.pg.demo.category.dataobject.Category();
        rootCategory.setId(0L)
        rootCategory.setName("ParentRoot")
        rootCategory.setStatus(com.pg.demo.category.enu.CategoryStatus.ACTIVE)

        def category1 = new com.pg.demo.category.dataobject.Category();
        category1.setId(1L)
        category1.setName("Apparel")
        category1.setStatus(com.pg.demo.category.enu.CategoryStatus.ACTIVE)
        category1.setParentCategory(rootCategory)
        category1.setParentId(0L)

        def category2 = new com.pg.demo.category.dataobject.Category();
        category2.setId(2L)
        category2.setName("Baby")
        category2.setStatus(com.pg.demo.category.enu.CategoryStatus.INACTIVE)
        category2.setParentCategory(category1)
        category2.setParentId(1L)

        def category3 = new com.pg.demo.category.dataobject.Category();
        category3.setId(3L)
        category3.setName("Baby Girl")
        category3.setStatus(com.pg.demo.category.enu.CategoryStatus.INACTIVE)
        category3.setParentCategory(category2)
        category3.setParentId(2L)

        category2.setChildCategories([category3])
        category1.setChildCategories([category2])
        rootCategory.setChildCategories([category1])

        when:
        def success = categoryService.fetchCategories(ids, true, true)

        then:
        1 * categoryRepository.findAllById(ids) >> [category3]
        1 * categoryRepository.findById(1L) >> Optional.of(category1)
        1 * categoryRepository.findById(2L) >> Optional.of(category2)
        1 * categoryRepository.findById(0L) >> Optional.of(rootCategory)

        def expectedParentCat = success.get(0)
        assert expectedParentCat.getId() == category1.getId()

        def expectedChildCat = expectedParentCat.getChildCategories().get(0)
        assert expectedChildCat.getId() == category2.getId()

        noExceptionThrown()

        where:
        ids  | fetchInActive | fetchParents
        [3L] | true          | true
    }

    def fetchDTO(com.pg.demo.category.dataobject.Category category) {

        def categoryDto = new Category()
        categoryDto.setId(category.getId())
        categoryDto.setParentId(category.getParentId())
        categoryDto.setChildCategories(category.setChildCategories())
        categoryDto.setName(category.getName())
        categoryDto.setStatus(category.getStatus())

        def parent = category.getParentCategory();
        if (parent != null) {
            categoryDto.setParent(fetchDTO(parent))
        }

        return categoryDto
    }

    @Unroll
    def "fetch entire category hierarchy"() {
        given:

        def rootCategory = new com.pg.demo.category.dataobject.Category();
        rootCategory.setId(0L)
        rootCategory.setName("ParentRoot")
        rootCategory.setStatus(com.pg.demo.category.enu.CategoryStatus.ACTIVE)


        def category1 = new com.pg.demo.category.dataobject.Category();
        category1.setId(1L)
        category1.setName("TestCat#1")
        category1.setLevel(com.pg.demo.category.enu.CategoryLevel.ONE)
        category1.setStatus(com.pg.demo.category.enu.CategoryStatus.ACTIVE)
        category1.setParentCategory(rootCategory)

        def category2 = new com.pg.demo.category.dataobject.Category();
        category2.setId(2L)
        category1.setLevel(com.pg.demo.category.enu.CategoryLevel.ONE)
        category2.setName("TestCat#2")
        //status is not set
        category2.setParentCategory(rootCategory)


        def category3 = new com.pg.demo.category.dataobject.Category();
        category3.setId(3L)
        category3.setName("TestCat#3")
        category1.setLevel(com.pg.demo.category.enu.CategoryLevel.ONE)
        category3.setStatus(com.pg.demo.category.enu.CategoryStatus.INACTIVE)
        category3.setParentCategory(rootCategory)

        def childCategories = [category1, category2, category3]
        rootCategory.setChildCategories(childCategories)


        when:
        def success = categoryService.fetchCategories(ids, includeInActive, includeHierarchy)

        then:
        1 * categoryRepository.fetchAll() >> [category1, category2, category3]

        success.size() == count

        where:
        ids  | includeInActive | includeHierarchy | count
        null | true            | null             | 3
        []   | false           | null             | 1
    }

    @Unroll
    def "fetch entire category hierarchy exception"() {
        given:

        when:
        def success = categoryService.fetchCategories(null, true, null)

        then:
        1 * categoryRepository.fetchAll() >> []

        thrown(BadRequestException.class)
    }

    @Unroll
    def "delete a category with children and/or offers"() {
        given:

        def category = new com.pg.demo.category.dataobject.Category();
        category.setId(id)
        category.setName(name)
        category.setStatus(status)
        category.setChildCategories(children)

        when:
        categoryService.deleteCategory(id)

        then:
        1 * categoryRepository.findById(id) >> Optional.of(category)
        offerClient.getCategoryOfferCount(id) >> offersTiedToCategory

        thrown(BadRequestException)

        where:
        id | name           | status                                         | children                                         | offersTiedToCategory
        1L | "testCategory" | com.pg.demo.category.enu.CategoryStatus.ACTIVE | [new com.pg.demo.category.dataobject.Category()] | 0
        1L | "testCategory" | com.pg.demo.category.enu.CategoryStatus.ACTIVE | []                                               | 1
        1L | "testCategory" | com.pg.demo.category.enu.CategoryStatus.ACTIVE | [new com.pg.demo.category.dataobject.Category()] | 1
        1L | "testCategory" | com.pg.demo.category.enu.CategoryStatus.ACTIVE | null                                             | 1
    }

    @Unroll
    def "delete a non existing category"() {
        given:

        def category = new com.pg.demo.category.dataobject.Category();
        category.setId(1L)
        category.setName("test cat")
        category.setStatus(com.pg.demo.category.enu.CategoryStatus.ACTIVE)
        category.setChildCategories(null)

        when:
        categoryService.deleteCategory(1L)

        then:
        1 * categoryRepository.findById(1L) >> Optional.empty()

        thrown(BadRequestException)
    }

    @Unroll
    def "delete a category with dependencies"() {
        given:

        def category = new com.pg.demo.category.dataobject.Category();
        category.setId(1L)
        category.setName("test cat")
        category.setStatus(com.pg.demo.category.enu.CategoryStatus.ACTIVE)
        category.setChildCategories(null)

        when:
        categoryService.deleteCategory(1L)

        then:
        1 * categoryRepository.findById(1L) >> Optional.of(category)
        offerClient.getCategoryOfferCount(1L) >> 0

        noExceptionThrown()
    }

     @Unroll
     def "save and update a category"() {
         given:
         def testCategory = new com.pg.demo.category.dataobject.Category();
         testCategory.setId(id)
         testCategory.setName(categoryName)
         testCategory.setStatus(status)

         def categoryDto = fetchDTO(testCategory)
         when:
         def success = categoryService.saveCategory(categoryDto, "test", null)

         then:
         categoryRepository.findById(1L) >> Optional.of(testCategory)
         categoryRepository.save(_ as com.pg.demo.category.dataobject.Category) >> testCategory

         success.getName() == categoryName
         success.getStatus() == status

         noExceptionThrown()

         where:
         id   | categoryName     | status
         1L   | "updateCategory" | com.pg.demo.category.enu.CategoryStatus.ACTIVE
         null | "newCategory"    | com.pg.demo.category.enu.CategoryStatus.INACTIVE
     }


    @Unroll
    def "try updating a non existing category"() {
        given:
        def testCategory = new com.pg.demo.category.dataobject.Category();
        testCategory.setId(id)
        testCategory.setName(categoryName)
        testCategory.setStatus(status)

        def categoryDto = fetchDTO(testCategory)
        when:
        categoryService.saveCategory(categoryDto, "test", null)

        then:
        categoryRepository.findById(1L) >> { throw new NoSuchElementException() }

        thrown(BadRequestException)

        where:
        id | categoryName     | status
        1L | "updateCategory" | com.pg.demo.category.enu.CategoryStatus.ACTIVE
    }
}