

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

@SuppressWarnings("unused")
public class WordCount2 {

    public static class TokenizerMapper extends
            Mapper<Object, Text, Text, IntWritable>
//            Ϊʲô����k1Ҫ��Object��Text��IntWritable�ȣ�������java��string����int�����ͣ���Ȼ��������������ģ������õĺô��ǣ���Ϊ������ʵ�������л��ͷ����л���
//            �������ڽڵ�䴫���ͨ��Ч�ʸ��ߡ����Ϊʲôhadoop����Ļ������͵ĵ�����
    
    
            //���Mapper����һ���������ͣ������ĸ��β����ͣ��ֱ�ָ��map�����������������ֵ������������ֵ�����͡�hadoopû��ֱ��ʹ��Java��Ƕ�����ͣ������Լ�������һ�׿����Ż��������л�����Ļ������͡�
            //��Щ���Ͷ���org.apache.hadoop.io���С�
            //������������е�Object���ͣ��������ֶ���Ҫʹ�ö������͵�ʱ��Text�����൱��Java�е�String���ͣ�IntWritable�����൱��Java�е�Integer����
            {
            //����������������˵�Ƕ����������󣬽з�������
        private final static IntWritable one = new IntWritable(1);//���1��ʾÿ�����ʳ���һ�Σ�map�����value����1.
                                    //��Ϊ��v1�ǵ��ʳ��ִ�����ֱ�Ӷ�one��ֵΪ1
        private Text word = new Text();
        
        public void map(Object key, Text value, Context context)
        //context����mapper��һ���ڲ��࣬�򵥵�˵�����ӿ���Ϊ����map����reduce�����и���task��״̬������Ȼ��MapContext���Ǽ�¼��mapִ�е������ģ���mapper���У����context���Դ洢һЩjob conf����Ϣ������job����ʱ�����ȣ�
        //���ǿ�����map�����д��������Ϣ����Ҳ��Hadoop�в���������һ���ܾ�������ӣ�ͬʱcontext��Ϊ��map��reduceִ���и���������һ�������������ƺ�Java web�е�session����application���������
        //�򵥵�˵context���󱣴�����ҵ���е���������Ϣ�����磺��ҵ������Ϣ��InputSplit��Ϣ������ID��
        //����������ֱ�۵ľ�����Ҫ�õ�context��write������
        //˵���ˣ�context�𵽵�������map��reduce���������������ĵ����ã�
        
                throws IOException, InterruptedException {
            //The tokenizer uses the default delimiter set, which is " \t\n\r": the space character, the tab character, the newline character, the carriage-return character
            StringTokenizer itr = new StringTokenizer(value.toString());//��Text���͵�valueת�����ַ�������
            
            //ʹ��StringTokenizer�ཫ�ַ�����hello,java,delphi,asp,PHP���ֽ�Ϊ��������
//            ��������н��Ϊ:
//                  hello
//                  java
//                  delphi
//                  asp
//                  php
            
            
            while (itr.hasMoreTokens()) {
//                ʵ���Ͼ���java.util.StringTokenizer.hasMoreTokens()
//                hasMoreTokens() ���������������Ƿ��д˱�����������ַ������ø���ı�ǡ�
                word.set(itr.nextToken());
                context.write(word, one);
            }
        }
    }

    public static class IntSumReducer extends
            Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();
        public void reduce(Text key, Iterable<IntWritable> values,
                Context context) throws IOException, InterruptedException {
            //����������ֱ�۵ľ�����Ҫ�õ�context��write������
            //˵���ˣ�context�𵽵�������map��reduce���������������ĵ����ã�
            
            int sum = 0;
            for (IntWritable val : values) {//������ǿ��forѭ����Ҳ��for����ѭ��
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();//�����ֻ��д��ôһ�仰���ͻ���ص�hadoop�������ļ���
        //Configuration�������ҵ�����ã���������mapred-site.xml��hdfs-site.xml��core-site.xml�������ļ���
        
        //ɾ���Ѿ����ڵ����Ŀ¼
        Path mypath = new Path("hdfs://Centpy:9000/test/wordcount-out");//���·��
        FileSystem hdfs = mypath.getFileSystem(conf);//�����ֻ��д��ôһ�仰���Ϳ��Ի�ȡ���ļ�ϵͳ�ˡ�
        //FileSystem��������ܶ�ϵͳ����������hdfs������Ϊ���������conf��Ŷ��ԭ����hadoop��Ⱥ������ʱ������֪����hdfs
        
        //����ļ�ϵͳ�д���������·������ɾ��������֤���Ŀ¼������ǰ���ڡ�
        if (hdfs.isDirectory(mypath)) {
            hdfs.delete(mypath, true);
        }
        
        //job����ָ������ҵִ�й淶����������������������ҵ�����С�
        Job job = Job.getInstance();// new Job(conf, "word count");
        job.setJarByClass(WordCount2.class);//������hadoop��Ⱥ��������ҵ��ʱ��Ҫ�Ѵ�������һ��jar�ļ���Ȼ�������ļ�
        //������Ⱥ�ϣ�Ȼ��ͨ��������ִ�������ҵ�����������в���ָ��JAR�ļ������ƣ�������������ͨ��job�����setJarByClass�����д���һ��������У�hadoop��ͨ��������������Ұ�������JAR�ļ���
        
        job.setMapperClass(TokenizerMapper.class);
        //job.setReducerClass(IntSumReducer.class);
        job.setCombinerClass(IntSumReducer.class);//Combiner���ղ���Ӱ��reduce����Ľ��
//                                ��仰Ҫ�ú����!!!
        
        
        
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        //һ�������mapper��reducer�����������������һ���ģ�������������������������У������һ�������ǾͿ������������������ָ��mapper�����key��value����������
        //job.setMapOutputKeyClass(Text.class);
        //job.setMapOutputValueClass(IntWritable.class);
        //hadoopĬ�ϵ���TextInputFormat��TextOutputFormat,����˵����������Բ������á�
        //job.setInputFormatClass(TextInputFormat.class);
        //job.setOutputFormatClass(TextOutputFormat.class);
        
        FileInputFormat.addInputPath(job, new Path(
                "hdfs://Centpy:9000/test/word.txt"));//FileInputFormat.addInputPath����ָ�������·�������ǵ����ļ���һ��Ŀ¼������ض��ļ�ģʽ��һϵ���ļ���
        //�ӷ������ƿ��Կ���������ͨ����ε������������ʵ�ֶ�·�������롣        
        FileOutputFormat.setOutputPath(job, new Path(
                "hdfs://Centpy:9000/hdfsOutput"));//ֻ����һ�����·������·��ָ���ľ���reduce��������ļ���д��Ŀ¼��
        //�ر�ע�⣺���Ŀ¼������ǰ���ڣ�����hadoop�ᱨ���ܾ�ִ����ҵ����������Ŀ���Ƿ�ֹ���ݶ�ʧ����Ϊ��ʱ�����е���ҵ�����������⸲�ǵ����ǿ϶�����������Ҫ��
        System.exit(job.waitForCompletion(true) ? 0 : 1);
        //ʹ��job.waitForCompletion�����ύ��ҵ���ȴ�ִ����ɣ��÷�������һ��booleanֵ����ʾִ�гɹ�����ʧ�ܣ��������ֵ��ת���ɳ����˳�����0��1���ò�����������һ����ϸ��ʶ��������ҵ��ѽ���д������̨��
        //waitForCompletion(���ύ��ҵ��ÿ�����ѯ��ҵ�Ľ��ȣ�������ֺ��ϴα�����иı䣬�Ͱѽ��ȱ��浽����̨����ҵ��ɺ�����ɹ�����ʾ��ҵ�����������ʧ����ѵ�����ҵʧ�ܵĴ������������̨
    }
}

//TextInputFormat��hadoopĬ�ϵ������ʽ�������̳���FileInputFormat,ʹ�����������ʽ��ÿ���ļ����ᵥ����ΪMap�����룬ÿ�����ݶ�������һ����¼��ÿ����¼���ʾ��<key��value>����ʽ��
//key��ֵ��ÿ�����ݼ�¼�����ݷ�Ƭ�е��ֽ�ƫ����������������LongWritable.
//value��ֵΪÿ�е����ݣ���������ΪText��
//
//ʵ����InputFormat�������������ɿɹ�Map�����<key��value>�ġ�
//InputSplit��hadoop���������������ݴ��͸�ÿ��������Map(Ҳ�������ǳ�˵��һ��split��Ӧһ��Map),
//InputSplit�洢�Ĳ������ݱ�������һ����Ƭ���Ⱥ�һ����¼����λ�õ����顣
//����InputSplit�ķ�������ͨ��InputFormat���������á�
//�����ݴ���Mapʱ��Map�Ὣ�����Ƭ���͸�InputFormat������InputFormat()�����getRecordReader()����RecordReader,RecordReader����ͨ��creatKey()��creatValue()�����ɹ�Map�����<key��value>�ԡ�
//
//OutputFormat()
//Ĭ�ϵ������ʽΪTextOutputFormat������Ĭ�������ʽ���ƣ��Ὣÿ����¼��һ�е���ʽ�����ı��ļ������ļ���ֵ������������ʽ�ģ���Ϊ�����ڲ������toString()������ֵת��ΪString�����������
