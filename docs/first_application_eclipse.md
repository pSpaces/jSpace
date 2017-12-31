
# Your first jSpace applications (with Eclipse)
If you have successfully [built and installed](getting_started.md) you can use Eclipse to develop your first jSpace Application. In fact you will use Eclipse as an IDE and Maven to manage dependencies. 

Open Eclipse and select _File->New->Project_ menu:

![Spaces, repositories and gates](figs/jspace_eclipse_tutorial_step1.png)

Select _Maven->Maven Project_:

![Spaces, repositories and gates](figs/jspace_eclipse_tutorial_step2.png)

Leave the default parameters and click _Next_:

![Spaces, repositories and gates](figs/jspace_eclipse_tutorial_step3.png)

The skeleton of a Maven project is generated. Open ```pom.xml``` file to add the dependency to jSpace:

![Spaces, repositories and gates](figs/jspace_eclipse_tutorial_step4.png)

Select the tab _Dependencies_:

![Spaces, repositories and gates](figs/jspace_eclipse_tutorial_step5.png)

Press button _Add_ to include the reference to jSpace. This opena dialog that you can fill as follow:

![Spaces, repositories and gates](figs/jspace_eclipse_tutorial_step6.png)

You can now open the file _App.java_ to code your first jSpace App:

![Spaces, repositories and gates](figs/jspace_eclipse_tutorial_step7.png)

Add the following code:

```
package com.mycompany.myjspaceapp;

import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

public class App {

	public static void main(String[] argv) throws InterruptedException {
		Space inbox = new SequentialSpace();

		inbox.put("Hello World!");
		Object[] tuple = inbox.get(new FormalField(String.class));				
		System.out.println(tuple[0]);

	}

}
```

At this point you can exectute your application:

![Spaces, repositories and gates](figs/jspace_eclipse_tutorial_step8.png)
