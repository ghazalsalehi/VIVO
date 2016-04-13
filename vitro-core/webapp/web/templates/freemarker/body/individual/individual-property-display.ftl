<#-- Default individual profile page template -->
 
<#import "lib-list.ftl" as l>
<#import "lib-properties.ftl" as p>

<#assign editable = individual.editable>
<#assign propertyGroups = individual.propertyList>

<#list propertyGroups.all as group>
	<#list group.properties as property>
		<#if property.localName == "${propertyName}">
            <#-- data property -->
            <#if property.type == "data">
                <@p.dataPropertyList property editable />
            <#-- object property -->
            <#else>
                <@p.objectProperty property editable />
            </#if>
		</#if>
	</#list>
</#list>

