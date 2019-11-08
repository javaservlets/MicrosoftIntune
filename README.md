
# Device Posture

A simple authentication node for ForgeRock's [Identity Platform][forgerock_platform] 6.5 and above. This node... **SHORT DESCRIPTION HERE**


Copy the .jar file from the ../target directory into the ../web-container/webapps/openam/WEB-INF/lib directory where AM is deployed.  Restart the web container to pick up the new node.  The node will then appear in the authentication trees components palette.


cURL statements to be used for testing:
getting an access token:

curl -X POST \
  https://login.microsoftonline.com/94781b09-3000-41eb-93bc-c7915241c40e/oauth2/v2.0/token \
  -H 'Accept: */*' \
  -H 'Accept-Encoding: gzip, deflate' \
  -H 'Cache-Control: no-cache' \
  -H 'Connection: keep-alive' \
  -H 'Content-Length: 275' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -H 'Cookie: x-ms-gateway-slice=prod; stsservicecookie=ests; fpc=AtHZFoCUNT9JpXi2C8G9irlG0RR2AQAAAHOtV9UOAAAA' \
  -H 'Host: login.microsoftonline.com' \
  -H 'User-Agent: PostmanRuntime/7.19.0' \
  -H 'cache-control: no-cache' \
  -d 'Content-Type=application%2Fx-www-form-urlencoded&client_id=cb17ccd4-0e70-48dc-a694-e6910418c70b&client_secret=%5Ba)PaGdK1*%7C0Ci1q&scope=https%3A%2F%2Fgraph.microsoft.com%2F.default&grant_type=password&username=info%40javaservlets.onmicrosoft.com&password=Ch2019angeit!'
  
 retrieving a device ID's compliancy:
 curl -X GET \
   https://graph.microsoft.com/v1.0/deviceManagement/manageddevices/988f8437-99b4-470d-9b60-087b65dc1649 \
   -H 'Authorization: (the token from above)
 
 Scratch File:
 
 import com.example.microsoft.UserInfo;
 import org.forgerock.openam.annotations.sm.Attribute;
 
 class Scratch {
     public static void main(String[] args) {
         String device_id="988f8437-99b4-470d-9b60-087b65dc1649"; // noncompliant ipad id =
         String device_id2="32e2fd8d-aa20-4557-b862-cb915e5ad640";
 
         String msScope="https://graph.microsoft.com/.default";
         String msClientId="cb17ccd4-0e70-48dc-a694-e6910418c70b";
         String msClientSecret="[a)PaGdK1*|0Ci1q";
         String msTokenUrl="https://login.microsoftonline.com/94781b09-3000-41eb-93bc-c7915241c40e/oauth2/v2.0/token";
         String msComplianceUrl="https://graph.microsoft.com/v1.0/deviceManagement/manageddevices/";
         String msAdmin="info@javaservlets.onmicrosoft.com";
         String msPassword="Ch2019angeit!";
 
         UserInfo userinfo=new UserInfo(msTokenUrl, msScope, msAdmin, msPassword, msClientId, msClientSecret);
         userinfo.getStatus(msComplianceUrl, device_id);
 
 
     }
 }
 
The code in this repository has binary dependencies that live in the ForgeRock maven repository. Maven can be configured to authenticate to this repository by following the following [ForgeRock Knowledge Base Article](https://backstage.forgerock.com/knowledge/kb/article/a74096897).

**SPECIFIC BUILD INSTRUCTIONS HERE**

**SCREENSHOTS ARE GOOD LIKE BELOW**

![ScreenShot](./example.png)


The sample code described herein is provided on an "as is" basis, without warranty of any kind, to the fullest extent permitted by law. ForgeRock does not warrant or guarantee the individual success developers may have in implementing the sample code on their development platforms or in production configurations.

ForgeRock does not warrant, guarantee or make any representations regarding the use, results of use, accuracy, timeliness or completeness of any data or information relating to the sample code. ForgeRock disclaims all warranties, expressed or implied, and in particular, disclaims all warranties of merchantability, and warranties related to the code, or any service or software related thereto.

ForgeRock shall not be liable for any direct, indirect or consequential damages or costs of any type arising out of any action taken by you or others related to the sample code.

[forgerock_platform]: https://www.forgerock.com/platform/  
