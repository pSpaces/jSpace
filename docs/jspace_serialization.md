# How to serialize objects in jSpace

Components of a distributed jSpace application interact, by default, via [JSON](https://www.json.org) messages. This means that to store and retrieve an object from a _remote space_ this must be _serialized_ and _deserialized_ in JSON. 

To implement these features jSpace uses the [Gson library](https://github.com/google/gson/blob/master/UserGuide.md). Gson is a Java library that converts Java Objects into their JSON representation and viceversa. One of the main features of Gson is that it can work with arbitrary Java objects including pre-existing objects that you do not have source code of. 

Gson natively supports serialization of standard Java Objects, of unmodifiable objects, and of [POJOs](https://en.wikipedia.org/wiki/Plain_old_Java_object). In fact, the large part of Java Objects can be serialized/deserialized with Gson without any customization. 

However, in some cases, the default Gson approach is not enough and _ad hoc_ mechanisms must be developed. This holds when complex data structures are used. 

Gson, and jSpace, uses a _factory_ approach to simplify the integration of customized serializer/deserializer. These are classes that must implement the interfaces ```JsonSerializer``` and ```JsonDeserializer``` (see [Gson documentation](https://github.com/google/gson/blob/master/UserGuide.md#TOC-Custom-Serialization-and-Deserialization) for further details). Both these interfaces are parametrized with respect to the type ```T``` of objects to serialize/deserialize. 

When you have developed the customized serializer you have just to register your classes in jSpace. This can be done via the class ```org.jspace.io.json.jSonUtils```. This class follows a [singleton pattern](https://en.wikipedia.org/wiki/Singleton_pattern). The reference to the (single) instance of the class can be obtained by using the static method ```jSonUtils.getInstance()```. 
This instance can be used to register the appropriate JSON serializer/deserializer for your class:

```
jSonUtils utils = jSonUtils.getInstance();
utils.register( classuri , yourclass.class , YourSerializer , YourDeserializer ); 
```

Above ```classuri``` is a uri that is used to identify the datatype associated with your class ```yourclass```. Note that this value allow the exchange of data among components developed with different langauges (other pSpaces languagews implement a similar approach). This registration code should be executed at the initialization phase of your app.  

An example of serializer/deserializer can be found in the jSpace code. Indeed, [Tuple](https://github.com/pSpaces/jSpace/blob/master/common/src/main/java/org/jspace/Tuple.java) and [Template](https://github.com/pSpaces/jSpace/blob/master/common/src/main/java/org/jspace/Template.java). In jSpace customized serializer and deserializer have been developed to simplify the exchanged ot tuples/templates: [TupleSerializer](https://github.com/pSpaces/jSpace/blob/master/common/src/main/java/org/jspace/io/json/TupleSerializer.java), [TupleDeserializer](https://github.com/pSpaces/jSpace/blob/master/common/src/main/java/org/jspace/io/json/TupleDeserializer.java),  [TemplateSerializer](https://github.com/pSpaces/jSpace/blob/master/common/src/main/java/org/jspace/io/json/TemplateSerializer.java), [TemplateSerializer](https://github.com/pSpaces/jSpace/blob/master/common/src/main/java/org/jspace/io/json/TemplateDeserializer.java).



