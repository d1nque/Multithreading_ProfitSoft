package org.example.model;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class ModifiedVersion {
    private Map<String, Double> fineStat = new HashMap<>();

    /**
     * Метод для сортировки нашего Map и подальшей записи в файл xml
     * @param pathToFolderWithJson путь к папке с файлами json
     * @throws InterruptedException
     */

    public void sortAndWriteFineStatWithThreads(String pathToFolderWithJson, int countOfThreads) throws InterruptedException, IOException {
        getSummaryOfFinesFromFilesUsingAsync(pathToFolderWithJson, countOfThreads);
        List<Map.Entry<String, Double>> nlist = new ArrayList<>(fineStat.entrySet());
        nlist.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        Fines fines = new Fines();
        fines.setFines(getListFinesFromMapEntry(nlist));
        jacksonAnnotation2Xml(fines);
    }
    public void sortAndWriteFineStat(String pathToFolderWithJson) throws InterruptedException, IOException {
        getSummaryOfFinesFromFiles(pathToFolderWithJson);
        List<Map.Entry<String, Double>> nlist = new ArrayList<>(fineStat.entrySet());
        nlist.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        Fines fines = new Fines();
        fines.setFines(getListFinesFromMapEntry(nlist));
        jacksonAnnotation2Xml(fines);
    }

    /**
     * Мне показалось самым простым способом для заполнения
     * структурированого и читаемого xml это создать отдельный описаный класс
     * @param nlist наш список на основе Map
     * @return наши штрафы в виде списка
     */
    private List<Fine> getListFinesFromMapEntry(List<Map.Entry<String, Double>> nlist){
        List<Fine> fineList = new LinkedList<>();
        for(int i = 0;i < nlist.size();i++){
            fineList.add(new Fine(nlist.get(i).getKey(), nlist.get(i).getValue()));
        }
        return fineList;
    }

    /**
     * Метод где мы перед путь к папке с файлами json
     * Пробегаемся по нашим файлам и собираем только тип и сумму штрафа
     * Использую Map, ключ уникальный, удобно, просто плюсуем штрафы
     * @param pathToFolderWithJson
     * @throws IOException
     */

    private void getSummaryOfFinesFromFilesUsingAsync(String pathToFolderWithJson, int coutOfThreads) throws InterruptedException, IOException {
        final ExecutorService executorService = Executors.newFixedThreadPool(16);
        File folderWithJson = new File(pathToFolderWithJson);
        //try with resources and using a Stream over the files (`Path`s) in the directory:
        try(Stream<Path> paths = Files.list(folderWithJson.toPath())) {
            final CompletableFuture<?>[] all = paths
                    //each Path is mapped to a CompletableFuture, to be run on the ExecutorService:
                    .map(path -> CompletableFuture.runAsync(() -> {
                        try {
                            getFineToStat(path.toFile());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }, executorService))
                    //we collect them into a new array, so that we can use them later
                    .toArray(CompletableFuture[]::new);
            //this will wait for all to finish:
            CompletableFuture.allOf(all).join();
            executorService.shutdown();
        }
    }


    private void getSummaryOfFinesFromFiles(String pathToFolderWithJson) throws IOException {
        File folderWithJson = new File(pathToFolderWithJson);

        for(File file: Objects.requireNonNull(folderWithJson.listFiles())) {
            for (Fine fine : jacksonFileParserForFine(file)) {
                if (getFileExtension(file).equals("json")) {
                    if (!fineStat.containsKey(fine.type)) {
                        fineStat.put(fine.type, fine.fine_amount);
                    } else {
                        fineStat.put(fine.type, fineStat.get(fine.type) + fine.fine_amount);
                    }
                }
            }
        }
    }

    private void getFineToStat(File file) throws IOException {
        for (Fine fine : jacksonFileParserForFine(file)) {
            if (getFileExtension(file).equals("json")) {
                if (!fineStat.containsKey(fine.type)) {
                    fineStat.put(fine.type, fine.fine_amount);
                } else {
                    fineStat.put(fine.type, fineStat.get(fine.type) + fine.fine_amount);
                }
            }
        }
    }
    private void jacksonAnnotation2Xml(Fines fines) {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            xmlMapper.writeValue(new File("src/main/resources/xml/fineResult.xml"), fines);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private List<Fine> jacksonFileParserForFine(File file) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jsonMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        return jsonMapper.readValue(file, new TypeReference<>(){});

    }

    /**
     * Метод для случая, если в папке будут файлы не только типа json
     * @param file
     * @return расширение файла
     */
    private static String getFileExtension(File file) {
        String fileName = file.getName();

        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0){
            return fileName.substring(fileName.lastIndexOf(".")+1);
        }
        else {
            return "";
        }
    }

}



//            CompletableFuture.supplyAsync(() -> file, executorService)
//                    .thenAccept(e -> {
//                    try {
//                    getFineToStat(e);
//                    System.out.println(Thread.currentThread().getName());
//                    } catch (Exception ex) {
//                    ex.printStackTrace();
//                    }
//                    }).join();

