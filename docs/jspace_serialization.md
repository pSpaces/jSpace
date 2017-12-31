# How to serialize objects in jSpace

Components of a distribuged jSpace application interact, by default, via [JSON](https://www.json.org) messages. This means that to store and retrieve an object from a _remote space_ this must be _serialized_ and _deserialized_ in JSON. 

To implement these features jSpace uses the [Gson library](https://github.com/google/gson/blob/master/UserGuide.md). Gson is a Java library that converts Java Objects into their JSON representation and viceversa. One of the main features of Gson is that it can work with arbitrary Java objects including pre-existing objects that you do not have source code of. 

