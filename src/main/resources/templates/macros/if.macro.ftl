<#macro if functionName parameters>
    if(${functionName}(<#list parameters as param>${param}<#if param_has_next>, </#if></#list>)) {
        <#nested>
    }
</#macro>