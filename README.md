# Simple permissions

[![Maven Central](https://img.shields.io/maven-central/v/io.github.edmondantes/simple-permissions?color=green&style=flat-square)](https://search.maven.org/search?q=g:io.github.edmondantes%20a:simple-localization)
![GitHub](https://img.shields.io/github/license/edmondantes/simple-permissions?color=green&style=flat-square)
![Lines of code](https://img.shields.io/tokei/lines/github/edmondantes/simple-permissions?style=flat-square)

---

### Lightweight library for managing permissions in your application

# Table of content

1. [How to add this library to your project](#how-to-add-this-library-to-your-project)
2. [Getting started](#getting-started)
3. [PermissionOwner](#permissionowner)
4. [DefaultPermissionManager customization](#defaultpermissionmanager-customization)
   1. [PermissionGroupStorage](#permissiongroupstorage)
   2. [PermissionNodeStorage](#permissionnodestorage)
   3. [PermissionStorage](#permissionstorage)
   4. [GroupReplacer](#groupreplacer)
   5. [PermissionMapperToNodeValue](#permissionmappertonodevalue)
   6. [Default group](#default-group)
   7. [Admin group](#admin-group)

## How to add this library to your project

### Maven

```xml

<dependency>
    <groupId>io.github.edmondantes</groupId>
    <artifactId>simple-permissions</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Gradle (kotlin)

```kotlin
implementation("io.github.edmondantes:simple-permissions:0.1.0")
```

### Gradle (groove)

```groovy
implementation "io.github.edmondantes:simple-permissions:0.1.0"
```

## Getting started

The main class for managing is `PermissionManager`. You can use default implementation: `DefaultPermissionManager`.

```kotlin
val supportPermissions = listOf("permission.one", "permission.two")
val permissionManager = DefaultPermissionManager(
    InMemoryPermissionGroupStorage(),
    InMemoryPermissionNodeStorage(),
    ConstantPermissionStorage(supportPermissions),
    defaultGroupName = "default",
    superAdminGroupName = "admin"
)

permissionManager.grant("default", "permission.one")

//  ...

permissionManager.group("default").checkPermission("permission.one")
```

## PermissionOwner

Method `group` and `groupOrDefault` in `PermissionManager` return object of class `PermissionOwner`. This class helps to
check which permissions was granted to specified group.

## DefaultPermissionManager customization

`DefaultPermissionManager` has 7 arguments in constructor, so you can customize they.

### PermissionGroupStorage

Store information about groups Default implementation: `InMemoryPermissionGroupStorage` - store data in variables

### PermissionNodeStorage

Store information about permissions in groups Default implementation: `InMemoryPermissionNodeStorage` - store data in
variables

### PermissionStorage

Store information about support permissions Default implementation: `ConstantPermissionStorage` - It is created from
list of permissions and store it.

### GroupReplacer

It will be called if user want to replace groups. `GroupReplacer` should replace one group to another. Default
implementation: `EmptyGroupReplacer` - It doesn't do anything

### PermissionMapperToNodeValue

It helps to transform string to permissions parts. Default implementation: `DefaultPermissionMapperToNodeValue` - It
transforms string with default format to permissions parts

## Default group

You can set default group name. For default this feature is disabled.

## Admin group

WARNING!!! This group has been granted all permissions!!!
You can set admin group name. For default this feature is disabled.
