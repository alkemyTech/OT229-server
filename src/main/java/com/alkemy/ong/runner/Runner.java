package com.alkemy.ong.runner;

import com.alkemy.ong.entities.Organization;
import com.alkemy.ong.repositories.OrganizationsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

    private final OrganizationsRepository organizationsRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!organizationsRepository.existsByName("Somos Más")) {
            Organization org = new Organization();
            org.setName("Somos Más");
            org.setWelcomeText("Bienvenido a Somos Más, la institucion social de La Cava, Cordoba");
            org.setPhone(1160112988);
            org.setUrlInstagram("SomosMás");
            org.setUrlFacebook("Somos_Más");
            org.setEmail("somosfundacionmas@gmail.com");
            org.setImage("src/SomosMas.jpg");
            org.setAddress("La Cava, Cordoba, Argentina");
            org.setAboutUsText("Desde 1997 en Somos Más trabajamos con los chicos y chicas," + "\n" +
                    " mamás y papás, abuelos y vecinos del barrio La Cava generando" + "\n" +
                    " procesos de crecimiento y de inserción social. Uniendo las manos de" + "\n" +
                    "todas las familias, las que viven en el barrio y las que viven fuera de" + "\n" +
                    " él, es que podemos pensar, crear y garantizar estos procesos. Somos" + "\n" +
                    "una asociación civil sin fines de lucro que se creó en 1997 con la" + "\n" +
                    " intencion de dar alimento a las familias de barrio. Con el tiempo" + "\n" +
                    "fuimos involucrándonos con la comunidad y agrandando y mejorando" + "\n" +
                    " nuestra capacidad de trabajo. Hoy somos un centro comunitario que " + "\n" +
                    "acompaña a más de 700 personas a través de las areas de:" + "\n" +
                    "Educación, deportes, primera infancia, salud, alimentación y trabajo" + "\n" +
                    "social  ");
            organizationsRepository.save(org);
        }
    }

    public Runner(OrganizationsRepository organizationsRepository) {
        this.organizationsRepository = organizationsRepository;
    }
}
