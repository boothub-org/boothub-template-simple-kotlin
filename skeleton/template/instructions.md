### Getting started

{{#if ghApiUsed}}
```
git clone https://github.com/{{ghProjectOwner}}/{{ghProjectId}}.git
cd {{ghProjectId}}
```
{{else}}
Download the generated zip file and unpack it. In the {{ghProjectId}} directory execute:
{{/if}}


&#8226; *On Linux or Mac OS:*
```
./gradlew build
```

&#8226; *On Windows:*
```
gradlew build
```



See the template documentation for more info.
