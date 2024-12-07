<#import "macros/if.macro.ftl" as macros>
package ${packageName};

<#if imports?has_content>
    <#list imports as import>
        import ${import};
    </#list>
</#if>

public class ${className?capitalize} {
    public static void main(String[] args) {
        <@macros.if functionName="empty" parameters=[]>
            System.out.println("my timtams shall not be fucked");
            return;
        </@macros.if>

        <@macros.if functionName="parameter" parameters=["${p1}"]>
            System.out.println("my timtams shall not be fucked with singleton");
            return;
        </@macros.if>

        <@macros.if functionName="parameter" parameters=["${p1}", "${p2}"]>
            System.out.println("my timtams shall not be fucked with singleton");
            return;
        </@macros.if>
    }

    private static boolean empty() {
        return true;
    }

    private static boolean parameter(String p1) {
        return p1 == "kaki";
    }

    private static boolean parameters(String p1, String p2) {
        return p1 == "kaki" && p2 == "kaki";
    }
}