# ppf

PPF is Parallel Programming Framework.
It provide some method to solve some common problems.

## Problem one

when we execute multi tasks, and these tasks has not relations with each other, we can parallel to get better performance.

```java
public Product getProductInfo(String id) {
    Product product = getFromDB(id);

    // fill product info by id
    product.setA(serviceA.getA(product.getAId()));

    product.setB(serviceA.getB(product.getBId()));
        
    product.setC(serviceA.getC(product.getCId()));

    return product;
}
```

```java
public Product getProductInfoParallel(String id) {
    final Product product = getFromDB(id);

    // parallel fill 
    ppf.aysncAll(
            () -> product.setA(serviceA.getA(product.getAId())),
            () -> product.setB(serviceB.getB(product.getBId())),
            () -> product.setC(serviceC.getC(product.getCId()))
    );

    return product;
}
```

## Problem two

When we have some data to handle, first we need get data from other datasource, 
then handle them and save the result, repeat this until all data is checked.
However if the data is too large, we will spend lots of time and memory for this,
maybe throw OutOfMemory error.

By use batch parallel handle, we may be spend more time but save more memory,
and we can improve the performance by add more memory.

```java
public void handle(String batchId) {
    
    List<Product> allData = getAllFromDB(batchId);
    
    // do some check task
    handleData(allData);

    saveResult(allData);
}

```

```java
public void parallelHandle(String batchId) {
    
    Iterator<List<Product>> iterator = getFromDB(batch);
    
    ppf.preload(
            iterator,
            data -> checkData(data),
            10
    );
}
```

## Problem three

When we does some thing very cost time, if one is finish, others will stop immediately.

```java
public void check(List<Data> list) {

    for (Data d : list) {
        if (!nameService.valid(d)) {
            throw new RuntimeException("xxxx");
        }
        if (!statusService.valid(d)) {
            throw new RuntimeException("yyyy");
        }
        // ...
    }
}
```

```java
public void parallelCheck(List<Data> list) {
    ppf.mostOnce(list, t -> {
        if (!nameService.valid(d)) {
            throw new RuntimeException("xxxx");
        }
        if (!statusService.valid(d)) {
            throw new RuntimeException("yyyy");
        }
        // ...
    });
}
```