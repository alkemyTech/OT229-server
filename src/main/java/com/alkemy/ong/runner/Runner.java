package com.alkemy.ong.runner;

import com.alkemy.ong.entities.ActivityEntity;
import com.alkemy.ong.entities.Organization;
import com.alkemy.ong.entities.Role;
import com.alkemy.ong.entities.User;
import com.alkemy.ong.exception.CloudStorageClientException;
import com.alkemy.ong.exception.CorruptedFileException;
import com.alkemy.ong.repositories.ActivityRepository;
import com.alkemy.ong.repositories.OrganizationsRepository;
import com.alkemy.ong.repositories.RoleRepository;
import com.alkemy.ong.repositories.UserRepository;
import com.alkemy.ong.services.CloudStorageService;
import com.amazonaws.util.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class Runner implements CommandLineRunner {

    private final int BASIC_ACTIVITIES_AMMOUNT = 4;
    private final CloudStorageService amazonS3Service;
    private final OrganizationsRepository organizationsRepository;
    private final ActivityRepository activityRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        polulateOrganization();
        populateActivities();
        createRolesUsersAndAdmins();
    }

    private void createRolesUsersAndAdmins() throws CloudStorageClientException, CorruptedFileException,
            IOException {

        Set<Role> roleUserPrivileges = new LinkedHashSet<>();
        Set<Role> rolesAdminPrivileges = new LinkedHashSet<>();


        Role role = new Role();
        if (!roleRepository.existsByName("ROLE_USER")) {

            role.setName("ROLE_USER");
            role.setDescription("Este rol es para los usuarios comunes");
            role = roleRepository.save(role);
        } else {
            role = roleRepository.findByName("ROLE_USER").get();
        }
        roleUserPrivileges.add(role);
        rolesAdminPrivileges.add(role);

        Role role2 = new Role();

        if (!roleRepository.existsByName("ROLE_ADMIN")) {
            role2.setName("ROLE_ADMIN");
            role2.setDescription("Este rol es para los ADMINISTRADORES");
            role2 = roleRepository.save(role2);
        } else {
            role2 = roleRepository.findByName("ROLE_ADMIN").get();
        }

        rolesAdminPrivileges.add(role2);

        populateRolesUsersAndAdmins(roleUserPrivileges,rolesAdminPrivileges);
    }

    private void populateRolesUsersAndAdmins(Set<Role> roleUserPrivileges, Set<Role> rolesAdminPrivileges) throws IOException,
            CloudStorageClientException, CorruptedFileException {

        if (!userRepository.existsByEmail("juanperez@gmail.com")) {
            User user = new User();

            user.setFirstName("Juan");
            user.setLastName("Perez");
            user.setEmail("juanperez@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));

            File file = new File("src/main/resources/images/userImage.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MockMultipartFile multipartFile = new MockMultipartFile("User1", file.getName(), mimeType, IOUtils.toByteArray(input));

            user.setPhoto(amazonS3Service.uploadFile(multipartFile));

            user.setRoleId(roleUserPrivileges);

            userRepository.save(user);
        }



        if (!userRepository.existsByEmail("tomastedeschini@gmail.com")) {
            User user = new User();

            user.setFirstName("Tomas");
            user.setLastName("Tedeschini");
            user.setEmail("tomastedeschini@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));


            File file = new File("src/main/resources/images/userImage.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("User1", file.getName(), mimeType, IOUtils.toByteArray(input));

            user.setPhoto(amazonS3Service.uploadFile(multipartFile));

            user.setRoleId(roleUserPrivileges);

            userRepository.save(user);
        }

        if (!userRepository.existsByEmail("lucasgonzalez@gmail.com")) {
            User user = new User();

            user.setFirstName("Lucas");
            user.setLastName("Gonzalez");
            user.setEmail("lucasgonzalez@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));


            File file = new File("src/main/resources/images/userImage.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("User1", file.getName(), mimeType, IOUtils.toByteArray(input));

            user.setPhoto(amazonS3Service.uploadFile(multipartFile));

            user.setRoleId(roleUserPrivileges);

            userRepository.save(user);
        }

        if (!userRepository.existsByEmail("matiasramirez@gmail.com")) {
            User user = new User();

            user.setFirstName("Matias");
            user.setLastName("Ramirez");
            user.setEmail("matiasramirez@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));


            File file = new File("src/main/resources/images/userImage.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("User1", file.getName(), mimeType, IOUtils.toByteArray(input));

            user.setPhoto(amazonS3Service.uploadFile(multipartFile));

            user.setRoleId(roleUserPrivileges);

            userRepository.save(user);

        }

        if (!userRepository.existsByEmail("pedrogoyena@gmail.com")) {
            User user = new User();

            user.setFirstName("Pedro");
            user.setLastName("Goyena");
            user.setEmail("pedrogoyena@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));


            File file = new File("src/main/resources/images/userImage.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("User1", file.getName(), mimeType, IOUtils.toByteArray(input));

            user.setPhoto(amazonS3Service.uploadFile(multipartFile));

            user.setRoleId(roleUserPrivileges);

            userRepository.save(user);

        }

        if (!userRepository.existsByEmail("cristiancastro@gmail.com")) {
            User user = new User();

            user.setFirstName("Cristian");
            user.setLastName("Castro");
            user.setEmail("cristiancastro@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));


            File file = new File("src/main/resources/images/userImage.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("User1", file.getName(), mimeType, IOUtils.toByteArray(input));

            user.setPhoto(amazonS3Service.uploadFile(multipartFile));

            user.setRoleId(roleUserPrivileges);

            userRepository.save(user);

        }

        if (!userRepository.existsByEmail("gustavoalfaro@gmail.com")) {
            User user = new User();

            user.setFirstName("Gustavo");
            user.setLastName("Alfaro");
            user.setEmail("gustavoalfaro@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));


            File file = new File("src/main/resources/images/userImage.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("User1", file.getName(), mimeType, IOUtils.toByteArray(input));

            user.setPhoto(amazonS3Service.uploadFile(multipartFile));

            user.setRoleId(roleUserPrivileges);

            userRepository.save(user);

        }

        if (!userRepository.existsByEmail("juanminujin@gmail.com")) {
            User user = new User();

            user.setFirstName("Juan");
            user.setLastName("Minujin");
            user.setEmail("juanminujin@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));


            File file = new File("src/main/resources/images/userImage.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("User1", file.getName(), mimeType, IOUtils.toByteArray(input));

            user.setPhoto(amazonS3Service.uploadFile(multipartFile));

            user.setRoleId(roleUserPrivileges);

            userRepository.save(user);

        }

        if (!userRepository.existsByEmail("estebanquito@gmail.com")) {
            User user = new User();

            user.setFirstName("Esteban");
            user.setLastName("Quito");
            user.setEmail("estebanquito@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));


            File file = new File("src/main/resources/images/userImage.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("User1", file.getName(), mimeType, IOUtils.toByteArray(input));

            user.setPhoto(amazonS3Service.uploadFile(multipartFile));

            user.setRoleId(roleUserPrivileges);

            userRepository.save(user);

        }

        if (!userRepository.existsByEmail("eladiocarrion@gmail.com")) {
            User user = new User();

            user.setFirstName("Eladio");
            user.setLastName("Carrion");
            user.setEmail("eladiocarrion@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));


            File file = new File("src/main/resources/images/userImage.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("User1", file.getName(), mimeType, IOUtils.toByteArray(input));

            user.setPhoto(amazonS3Service.uploadFile(multipartFile));

            user.setRoleId(roleUserPrivileges);

            userRepository.save(user);

        }

        if (!userRepository.existsByEmail("ezequielgiussani@gmail.com")) {
            User user = new User();

            user.setFirstName("Ezequiel");
            user.setLastName("Giussani");
            user.setEmail("ezequielgiussani@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));


            File file = new File("src/main/resources/images/userImage.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("User1", file.getName(), mimeType, IOUtils.toByteArray(input));

            user.setPhoto(amazonS3Service.uploadFile(multipartFile));

            user.setRoleId(rolesAdminPrivileges);

            userRepository.save(user);

        }

        if (!userRepository.existsByEmail("francoroman@gmail.com")) {
            User user = new User();

            user.setFirstName("Franco");
            user.setLastName("Roman");
            user.setEmail("francoroman@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));


            File file = new File("src/main/resources/images/userImage.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("User1", file.getName(), mimeType, IOUtils.toByteArray(input));

            user.setPhoto(amazonS3Service.uploadFile(multipartFile));

            user.setRoleId(rolesAdminPrivileges);

            userRepository.save(user);

        }

        if (!userRepository.existsByEmail("ivanpizarro@gmail.com")) {
            User user = new User();

            user.setFirstName("Ivan");
            user.setLastName("Pizarro");
            user.setEmail("ivanpizarro@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));


            File file = new File("src/main/resources/images/userImage.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("User1", file.getName(), mimeType, IOUtils.toByteArray(input));

            user.setPhoto(amazonS3Service.uploadFile(multipartFile));

            user.setRoleId(rolesAdminPrivileges);

            userRepository.save(user);

        }

        if (!userRepository.existsByEmail("brendadaffunchio@gmail.com")) {
            User user = new User();

            user.setFirstName("Brenda");
            user.setLastName("Daffunchio");
            user.setEmail("brendadaffunchio@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));


            File file = new File("src/main/resources/images/userImage.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("User1", file.getName(), mimeType, IOUtils.toByteArray(input));

            user.setPhoto(amazonS3Service.uploadFile(multipartFile));

            user.setRoleId(rolesAdminPrivileges);

            userRepository.save(user);

        }

        if (!userRepository.existsByEmail("camiloabarca@gmail.com")) {
            User user = new User();

            user.setFirstName("Camilo");
            user.setLastName("Abarca");
            user.setEmail("camiloabarca@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));


            File file = new File("src/main/resources/images/userImage.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("User1", file.getName(), mimeType, IOUtils.toByteArray(input));

            user.setPhoto(amazonS3Service.uploadFile(multipartFile));

            user.setRoleId(rolesAdminPrivileges);

            userRepository.save(user);

        }

        if (!userRepository.existsByEmail("fernandonesper@gmail.com")) {
            User user = new User();

            user.setFirstName("Fernando");
            user.setLastName("Nesper");
            user.setEmail("fernandonesper@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));


            File file = new File("src/main/resources/images/userImage.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("User1", file.getName(), mimeType, IOUtils.toByteArray(input));

            user.setPhoto(amazonS3Service.uploadFile(multipartFile));

            user.setRoleId(rolesAdminPrivileges);

            userRepository.save(user);

        }

        if (!userRepository.existsByEmail("guadalupereboiro@gmail.com")) {
            User user = new User();

            user.setFirstName("Guadalupe");
            user.setLastName("Reboiro");
            user.setEmail("guadalupereboiro@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));


            File file = new File("src/main/resources/images/userImage.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("User1", file.getName(), mimeType, IOUtils.toByteArray(input));

            user.setPhoto(amazonS3Service.uploadFile(multipartFile));

            user.setRoleId(rolesAdminPrivileges);

            userRepository.save(user);

        }

        if (!userRepository.existsByEmail("marianovergara@gmail.com")) {
            User user = new User();

            user.setFirstName("Mariano");
            user.setLastName("Vergara");
            user.setEmail("marianovergara@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));


            File file = new File("src/main/resources/images/userImage.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("User1", file.getName(), mimeType, IOUtils.toByteArray(input));

            user.setPhoto(amazonS3Service.uploadFile(multipartFile));

            user.setRoleId(rolesAdminPrivileges);

            userRepository.save(user);

        }

        if (!userRepository.existsByEmail("juanisantamarina@gmail.com")) {
            User user = new User();

            user.setFirstName("Juani");
            user.setLastName("Santamarina");
            user.setEmail("juanisantamarina@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));


            File file = new File("src/main/resources/images/userImage.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("User1", file.getName(), mimeType, IOUtils.toByteArray(input));

            user.setPhoto(amazonS3Service.uploadFile(multipartFile));

            user.setRoleId(rolesAdminPrivileges);

            userRepository.save(user);

        }

        if (!userRepository.existsByEmail("juanlopez@gmail.com")) {
            User user = new User();

            user.setFirstName("Juan");
            user.setLastName("Lopez");
            user.setEmail("juanlopez@gmail.com");
            user.setPassword(passwordEncoder.encode("1234"));


            File file = new File("src/main/resources/images/userImage.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("User1", file.getName(), mimeType, IOUtils.toByteArray(input));

            user.setPhoto(amazonS3Service.uploadFile(multipartFile));

            user.setRoleId(rolesAdminPrivileges);

            userRepository.save(user);

        }


    }


    private void populateActivities() throws IOException, CloudStorageClientException, CorruptedFileException {
        ActivityEntity[] activities = new ActivityEntity[BASIC_ACTIVITIES_AMMOUNT];
        initializeBasicActivities(activities);

        activities[0].setName("Programas Educativos");
        activities[0].setContent("Mediante nuestros programas educativos, buscamos" + "\n" +
                "incrementar la capacidad intelectual, moral y afectiva de las personas de " + "\n" +
                "acuerdo con la cultura y las normas de convivencia de la sociedad a la que" + "\n" +
                " pertenecen");

        File file = new File("src/main/resources/images/programas-educativos.jpg");
        String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
        FileInputStream input = new FileInputStream(file);
        MockMultipartFile multipartFile = new MockMultipartFile("programas-educativos", file.getName(), mimeType, IOUtils.toByteArray(input));
        activities[0].setImage(amazonS3Service.uploadFile(multipartFile));


        activities[1].setName("Apoyo escolar para el nivel Primario");
        activities[1].setContent("El espacio de apoyo escolar es el corazon del area educativa. Se realizan los" + "\n" +
                "talleres de lunes a jueves de 10 a 12 horas y de 14 a 16 horas en el " + "\n" +
                "contraturno. Los sabados tambien se realiza el taller para niños y niñas que" + "\n" +
                " asisten a la escuela doble turno. Tenemos un espacio especial para los del " + "\n" +
                "1er grado 2 veces por semana ya que ellos necesitan atencion especial!" + "\n" +
                "Actualmente se encuentran inscriptos a este programa 150 niños y niñas de" + "\n" +
                " 6 a 15 años. ESte taller esta pensado para ayudar a los alumnos con el" + "\n" +
                "Material que traen de la escuela, tambien tenemos una docente que les da" + "\n" +
                "clases de lengua y matematica con una planificacion propia que armamos" + "\n" +
                "en Manos para nivelar a los niños y que vayan con mas herramientas a la " + "\n" +
                "escuela.");
        file = new File("src/main/resources/images/apoyo-escolar-primario.jpg");
        mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
        input = new FileInputStream(file);
        multipartFile = new MockMultipartFile("apoyo-escolar-primario", file.getName(), mimeType, IOUtils.toByteArray(input));
        activities[1].setImage(amazonS3Service.uploadFile(multipartFile));


        activities[2].setName("Apoyo escolar para el nivel Secundario");
        activities[2].setContent("Del mismo modo que en primaria, este taller es el corazon del area" + "\n" +
                "secundaria. Se realizan talleres de lunes a viernes de 10 a 12 horas y de 16 a" + "\n" +
                " 18 horas en el contraturno. Actualmente se encuentran inscriptos en el taller" + "\n" +
                "50 adolescentes entre 13 y 20 años. Aqui los jovenes se presentan con el " + "\n" +
                "material que traen del colegio y una docente de la institucion y un grupo de" + "\n" +
                "voluntarios los recibe para ayudarlos a estudiar o hacer la tarea. Este " + "\n" +
                "espacio tambien es utilizado por los jovenes como un punto de encuentro y relacion entre ellos y la institucion. ");

        file = new File("src/main/resources/images/apoyo-escolar-secundario.jpg");
        mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
        input = new FileInputStream(file);
        multipartFile = new MockMultipartFile("apoyo-escolar-secundario", file.getName(), mimeType, IOUtils.toByteArray(input));
        activities[2].setImage(amazonS3Service.uploadFile(multipartFile));


        activities[3].setName("Tutorias");
        activities[3].setContent("Es un programa destinado a jovenes a partir del tercer año de secundaria," + "\n" +
                "cuyo objetivo es garantizar su permanencia en la escuela y contruir un" + "\n" +
                "proyecto de vida que da sentido al colegio. El objetivo de esta propuesta es" + "\n" +
                "lograr la integracion escolar de niños y jovenes del barrio promoviendo el " + "\n" +
                "soporte socioeducativo y emocional apropiado, desarrollando los recursos" + "\n" +
                "institucionales necesarios a traves de la articulacion de nuestras" + "\n" +
                "intervenciones con las escuelas que los alojan, con las familias de los" + "\n" +
                " alumnos y con las instancias municipales, provinciales y nacionales que" + "\n" +
                "correspondan. El programa contempla:" + "\n" +
                ". Encuentro semananl con tutores (Individuales y grupales)" + "\n" +
                ".Actividad proyecto (los participantes del programa deben pensar una" + "\n" +
                "actividad relacionada a lo que quieren hacer una vez terminada la" + "\n" +
                "escuela y su tutor los acopmpaña en el proceso)" + "\n" +
                ".Ayudantias (los que comienzan l 2do año del programa deben" + "\n" +
                "elegir una de las actividades que realiza la institucion para" + "\n" +
                "acompañarla e ir conociendo como es el mundo laboral que les espera)" + "\n" +
                "Acompañamiento escolar y familiar (los tutores son encargados de" + "\n" +
                "articular con la familia y con las escuelas de los jovenes para" + "\n" +
                "monitorear el estado de los tutoreados)" + "\n" +
                ".Beca estimulo (los jovenes reciben una beca estimulo que es " + "\n" +
                "supervisada por los tutores). Actualmente se encuentran inscriptos en" + "\n" +
                "el programa 30 adolescentes." + "\n" +
                ".Taller Arte y Cuentos: Taller literario y de manualidades que se realiza" + "\n" +
                "semanalmente." + "\n" +
                ".Paseos recreativos y educativos. Estos paseos estan pensados para" + "\n" +
                "promover la participacion y sentido de pertenencia de los niños, niñas" + "\n" +
                "y adolescentes al area educativa. ");

        file = new File("src/main/resources/images/tutorias.jpg");
        mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
        input = new FileInputStream(file);
        multipartFile = new MockMultipartFile("tutorias", file.getName(), mimeType, IOUtils.toByteArray(input));
        activities[3].setImage(amazonS3Service.uploadFile(multipartFile));

        compareActivitiesAndLoad(activities);
    }

    private void compareActivitiesAndLoad(ActivityEntity[] activities) {
        for (int i = 0; i < BASIC_ACTIVITIES_AMMOUNT; i++) {
            if (!activityRepository.existsByName(activities[i].getName())) {
                activityRepository.save(activities[i]);
            }
        }
    }

    private void initializeBasicActivities(ActivityEntity[] activities) {

        for (int i = 0; i < BASIC_ACTIVITIES_AMMOUNT; i++) {
            activities[i] = new ActivityEntity();
        }

    }

    private void polulateOrganization() throws IOException, CloudStorageClientException, CorruptedFileException {
        if (!organizationsRepository.existsByName("Somos Más")) {
            Organization org = new Organization();
            org.setName("Somos Más");
            org.setWelcomeText("Bienvenido a Somos Más, la institucion social de La Cava, Cordoba");
            org.setPhone(1160112988);
            org.setUrlInstagram("SomosMás");
            org.setUrlFacebook("Somos_Más");
            org.setEmail("somosfundacionmas@gmail.com");

            File file = new File("src/main/resources/images/fundacion-mas.png");
            String mimeType = new MimetypesFileTypeMap().getContentType(file.getName());
            FileInputStream input = new FileInputStream(file);
            MockMultipartFile multipartFile = new MockMultipartFile("somos-mas", file.getName(), mimeType, IOUtils.toByteArray(input));

            org.setImage(amazonS3Service.uploadFile(multipartFile));


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

    public Runner(OrganizationsRepository organizationsRepository,
                  CloudStorageService amazonS3Service,
                  ActivityRepository activityRepository,
                  UserRepository userRepository,
                  RoleRepository roleRepository,
                  PasswordEncoder passwordEncoder) {

        this.organizationsRepository = organizationsRepository;
        this.activityRepository = activityRepository;
        this.amazonS3Service = amazonS3Service;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
}