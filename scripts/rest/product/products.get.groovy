def result = [:]

def queryStatement = 'content-type:"/component/product"'

def query = searchService.createQuery()
    query.setQuery(queryStatement)
    // limit returned fields if you like
    //query.addParam("fl", "localid,sku,title,style,description,price,installments,freeShipping,sizes.item.key")

	query.addParam("sort", "localId asc")
def executedQuery = searchService.search(query)
def itemsFound = executedQuery.response.numFound

result.sum = 1000 + 2000
result.products = []

executedQuery.response.documents.eachWithIndex { document, idx ->
	def product = [ id:             idx,
    				cmsId:          document.localId,
    				sku:            document.sku, 
                    title:          document.title, 
                    style:          document.style,
                    description:    document.description,
                    price:          getPrice(document),        // potentially get the price from external system
                    installments:   getInventory(document),    // potentially get inventory from external system
                    isFreeShipping: document.freeShipping,
                    endorsedBy:     (document.endorsedBy) ? document.endorsedBy : "Hello World",
                    availableSizes: document["sizes.item.key"],
                    currencyId:     "USD",  // hard code USD for now
                    currencyFormat: "\$",   // hard code currency format for now
                    largeImage:     document.largeImage,
                    smallImage:     document.smallImage
                  ]
    
	result.products.add(product)
}

return result



def getPrice(product) {
	// simple example of abstracting where price comes from
	return new Float(product.price)
}

def getInventory(product) {
	def invServices = applicationContext.get("inventoryServices")
    return invServices.getInventoryByProductId(product.sku)
}
