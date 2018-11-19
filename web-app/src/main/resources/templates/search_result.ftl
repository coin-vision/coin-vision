<!DOCTYPE html>
<html>
<body>

    <h2>Search another image</h2>

    <table>
        <tr>
            <td>
                <form method="POST" action="/search-by-picture" enctype="multipart/form-data">
                    image file: <input type="file" name="file" /><br/><br/>
                    <input type="submit" value="Search by image file" />
                </form>
            </td>
        </tr>
    </table>
    
    <div align="center">
	    <table>
	        <tr>
	            <td>
				    <div align="center"><h1>Image you posted</h1></div>
				    <div align="center"><img width="600" src="data:image/jpg;base64,${originImage}" alt="Origin Image"></div>
	            </td>
	        </tr>
	    </table>
    </div>

    
    
    <br />
    <br />
    <br />
    <div align="center"><h1>Picture search result from coinshome.net</h1></div>
    <#list searchResult as entry>
          <a target="_blank"
        href="https://www.coinshome.net/en/coin_definition-a-b-c-${entry.coinId}.htm"><img src="https://d3k6u6bv48g1ck.cloudfront.net/fs/600_300/${entry.imageId}.jpg"></a>
        
        &nbsp;&nbsp;&nbsp;
    </#list>  

</body>
</html>
